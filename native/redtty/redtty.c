#include <stdio.h> 
#include <stdlib.h> 
#include <unistd.h> 
#include <errno.h> 
#include <string.h>
#include <ctype.h>
#include <sys/ioctl.h>

#ifdef LINUX 
#include <pty.h> 
#endif 

#ifdef __APPLE__
#include <stdint.h> 
#include <util.h> 
#endif

#define ESC '\033'
#define MAX_ESC_SEQUENCE_LENGTH	16

int 
main (void) 
{
	/* Read char */
	char buffer[1024+1];
	int buffer_index = 0;
	int read_count;
		
	/* Descriptor set for select */
	fd_set descriptor_set;
	
	int pty;
	
	switch (forkpty(&pty,  /* pseudo-terminal master end descriptor */ 
					 NULL,  /* This can be a char[] buffer used to get... */ 
							/* ...the device name of the pseudo-terminal */ 
					 NULL,  /* This can be a struct termios pointer used... */ 
					        /* to set the terminal attributes */ 
					 NULL)) /* This can be a struct winsize pointer used... */ 
    {                       /* ...to set the screen size of the terminal */ 
		case -1: /* Error */ 
			perror ("fork()"); 
			exit (EXIT_FAILURE); 
			
		case 0: /* This is the child process */ 
			execl("/bin/bash", "-bash", "-li", NULL); 
			
			perror("exec()"); /* Since exec* never return */ 
			exit (EXIT_FAILURE); 
			
		default: /* This is the parent process */
			while (1) 
			{ 
				FD_ZERO (&descriptor_set);
				FD_SET (STDIN_FILENO, &descriptor_set); 
				FD_SET (pty, &descriptor_set);
				
				if (select (FD_SETSIZE, &descriptor_set, NULL, NULL, NULL) < 0) 
				{ 
					perror ("select()"); 
					exit (EXIT_FAILURE); 
				}
				
				/* User typed something at STDIN */
				if (FD_ISSET (STDIN_FILENO, &descriptor_set)) 
				{
					read_count = read(STDIN_FILENO, &buffer[buffer_index], sizeof(buffer)-buffer_index-1);
					
					switch (read_count)
					{
						case -1:
							fprintf(stderr, "Disconnected.\n"); 
							exit (EXIT_FAILURE);
							break;
							
						case 0:
							fprintf (stderr, "Done\n"); 
							exit (EXIT_SUCCESS);
							break;
							
						default:
							buffer_index += read_count;
							buffer[buffer_index] = '\0';
							do {
								char *ch;
								int i = 0;
								
								ch = strchr(buffer, ESC);
								if (ch == NULL)
								{
									write(pty, &buffer, buffer_index);
									buffer_index = 0;
									break;
								}

								i = ch - buffer;
								if (i > 0) {
									write(pty, buffer, i);
									buffer_index -= i;
									memmove(buffer, ch, buffer_index);
									
								}
								for( i = 1; (i < buffer_index) && (buffer[i] != ESC) && !isalpha(buffer[i]); ++i)
								{
									/* noop */
								}
								if (i < buffer_index)
								{
									int param[4];
									switch (buffer[i])
									{
										case ESC:
											write(pty, buffer, i);
											--i;
											break;
										case 't':
											if ((sscanf(&buffer[1], "[%d;%d;%dt", &param[0], &param[1], &param[2]) == 3) && (param[0] == 8))
											{
												struct winsize size;
												size.ws_row = param[1];
												size.ws_col = param[2];
												ioctl(pty, TIOCSWINSZ, &size);
												break;
											}
										default:
											write(pty, buffer, i+1);
											break;
									}
									buffer_index -= i + 1;
									if (buffer_index > 0) {
										memmove(buffer, &buffer[i+1], buffer_index);
										continue;
									}
								} else {
									if (buffer_index > MAX_ESC_SEQUENCE_LENGTH) {
										write(pty, &buffer, buffer_index);
										buffer_index = 0;
									}
								}

							} while (0);
							break;
					}
				} 
				
				/* Output from the bash */
				if (FD_ISSET (pty, &descriptor_set)) 
				{
					read_count = read(pty, &buffer, sizeof(buffer)-1);
					
					switch (read_count)
					{
						case -1:
							fprintf (stderr, "Disconnected.\n"); 
							exit (EXIT_FAILURE); 
							break;
							
						case 0:
							fprintf (stderr, "Done\n"); 
							exit (EXIT_SUCCESS);
							break;
							
						default:
							write (STDOUT_FILENO, &buffer, read_count);
							break;
					}
				}
			}
    } 
	
	return EXIT_FAILURE; 
} 

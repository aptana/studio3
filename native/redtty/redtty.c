#include <stdio.h> 
#include <stdlib.h> 
#include <unistd.h> 
#include <errno.h> 
#include <string.h>
#include <sys/ioctl.h>

#ifdef LINUX 
#include <pty.h> 
#endif 

#ifdef __APPLE__
#include <stdint.h> 
#include <util.h> 
#endif

#define SET_SIZE "\033[8;"

int 
main (void) 
{
	/* Read char */
	char buffer[1024+1];
	char old_char;
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
					read_count = read(STDIN_FILENO, &buffer, sizeof(buffer)-1);
					
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
							buffer[read_count] = '\0';
							
							if (!strncmp(buffer, SET_SIZE, strlen(SET_SIZE)))
							{
								char *start = buffer + strlen(SET_SIZE);
								char *semi = strstr(start, ";");
								char *t = (semi != NULL) ? strstr(semi + 1, "t") : NULL;
								
								if (semi != NULL && t != NULL)
								{
									int height = strtol(start, &semi, 0);
									int width = strtol(semi + 1, &t, 0);
									
									struct winsize size;
									size.ws_row = height;
									size.ws_col = width;
									ioctl(pty, TIOCSWINSZ, &size);
									
									write(pty, t + 1, read_count - (t - buffer) - 1);
								}
								else
								{
									write(pty, &buffer, read_count);
								}
							}
							else
							{
								write(pty, &buffer, read_count);
							}
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

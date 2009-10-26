#include <stdio.h> 
#include <stdlib.h> 
#include <unistd.h> 
#include <errno.h> 
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <string.h>

#ifdef LINUX 
#include <pty.h> 
#endif 

#ifdef __APPLE__
#include <stdint.h> 
#include <util.h> 
#endif

int 
main (void) 
{
	/* Read char */
	char buffer[1024+1];
	int read_count;
		
	/* Descriptor set for select */
	fd_set descriptor_set;

#ifdef USE_CONTROL_SOCKET
#define PORT		8182
#define GETDIM		"GETDIM\n"
	int sd;
	struct sockaddr_in sa;
	struct hostent *host;
	
	if ((host = gethostbyname("localhost")) == 0) {
		perror("gethostbyname(localhost)");
		exit(EXIT_FAILURE);
	}
	
	memset(&sa, 0, sizeof(sa));
	sa.sin_family = AF_INET;
	sa.sin_addr.s_addr = ((struct in_addr *)(host->h_addr))->s_addr;
	sa.sin_port = htons(PORT);
	
	if ((sd = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
		perror("socket");
		exit(EXIT_FAILURE);
	}
	
	if (connect(sd, (struct sockaddr *)&sa, sizeof(sa)) == -1) {
		perror("connect");
		exit(EXIT_FAILURE);
	}
#endif
	
	int pty;
	
	//int stderr_fd = dup(STDERR_FILENO);
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
			//write(sd, buffer, sprintf(buffer, "%i\n", getpid()));
			
			//dup2(stderr_fd, 2);
			execl("/bin/bash", "-bash", "-li", NULL); 
			
			perror("exec()"); /* Since exec* never return */ 
			exit (EXIT_FAILURE); 
			
		default: /* This is the parent process */
			while (1) 
			{ 
				FD_ZERO (&descriptor_set);
				FD_SET (STDIN_FILENO, &descriptor_set); 
				FD_SET (pty, &descriptor_set);
#ifdef USE_CONTROL_SOCKET
				FD_SET (sd, &descriptor_set); 
#endif
				
				if (select (FD_SETSIZE, &descriptor_set, NULL, NULL, NULL) < 0) 
				{ 
					perror ("select()"); 
					exit (EXIT_FAILURE); 
				}
				
				// try forcing a resize
				//struct winsize size;
				//size.ws_row = 10;
				//size.ws_col = 40;
				//ioctl(pty, TIOCSWINSZ, &size);
				
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
							write(pty, &buffer, read_count);
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
				
#ifdef USE_CONTROL_SOCKET
				/* Input from socket */
				if (FD_ISSET (sd, &descriptor_set)) 
				{
					read_count = read(sd, &buffer, sizeof(buffer)-1);
					switch (read_count)
					{
						case -1:
							fprintf (stderr, "Socket disconnected.\n"); 
							exit (EXIT_FAILURE); 
							break;
							
						case 0:
							fprintf (stderr, "Socket done\n"); 
							exit (EXIT_SUCCESS);
							break;
							
						default:
							buffer[read_count] = '\0';
							if (!strncmp(buffer, GETDIM, strlen(GETDIM))) {
								write(sd, buffer, sprintf(buffer, "%i,%i\n", 10, 10));
							}
							break;
					}
				}
#endif
			}
    } 
	
	return EXIT_FAILURE; 
} 

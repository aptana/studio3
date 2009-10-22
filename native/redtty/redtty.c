#include <stdio.h> 
#include <stdlib.h> 
#include <unistd.h> 
#include <errno.h> 
#include <sys/types.h> 

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
	char c;
		
	/* Descriptor set for select */
	fd_set descriptor_set;

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
				
				if (select (FD_SETSIZE, &descriptor_set, NULL, NULL, NULL) < 0) 
				{ 
					perror ("select()"); 
					exit (EXIT_FAILURE); 
				}
				
				/* User typed something at STDIN */
				if (FD_ISSET (STDIN_FILENO, &descriptor_set)) 
				{ 
					if ( (read (STDIN_FILENO, &c, 1) != 1) || (write (pty, &c, 1) != 1) ) 
					{ 
						fprintf (stderr, "Done\n"); 
						exit (EXIT_SUCCESS); 
					} 
				} 
				
				/* Output from the bash */
				if (FD_ISSET (pty, &descriptor_set)) 
				{ 
					if ( (read (pty, &c, 1) != 1) || (write (STDOUT_FILENO, &c, 1) != 1) ) 
					{ 
						fprintf (stderr, "Disconnected.\n"); 
						exit (EXIT_FAILURE); 
					} 
				} 
			} 
    } 
	
	return EXIT_FAILURE; 
} 

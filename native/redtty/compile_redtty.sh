#Note: sudo apt-get install gcc-multilib is needed to compile 32bits (if on a 64 bit machine)
#Note: -lutil is needed for forkpty.

gcc redtty.c -o ../../plugins/com.aptana.terminal/os/linux/x86_64/redtty -lutil -m64
echo Compiled ../../plugins/com.aptana.terminal/os/linux/x86_64/redtty

gcc redtty.c -o ../../plugins/com.aptana.terminal/os/linux/x86/redtty -lutil -m32
echo Compiled ../../plugins/com.aptana.terminal/os/linux/x86/redtty

echo Compile finished

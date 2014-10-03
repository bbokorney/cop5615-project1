---- Usage -----
The program has been compiled into a jar. The jar can be run directly using java.

To run the program as a master, which will let it mine on it's own as well as let other slaves connect to the machine, use the command

java -jar project1.jar master 5

where the 'master' command indicates the program should run in master mode, and the '5' indicates that coins prefixed with 5 zeroes should be searched for. By default, the program will search for over ten million inputs. By default, the master will start it's own set of local workers, and thus can mine for coins on its own.

To run the program in slave mode, use the 'slave' command and specify the external IP address of the master. Note that the master program binds to the machine's external IP address, and thus using the localhost address of 127.0.0.1 will not allow a slave to connect to the master.

java -jar project1.jar slave 192.168.1.1

There are further options for both modes. For example, you can set the size of the work unit, the number of worker actors, start and end values for which to search over, and enable extra logging. For example, if you wanted to search 10,000 values with 4 worker actors and look for coins prefixed with 3 zeroes, you could specify

java -jar project1.jar master --start 1 --end 10000 --workers 4 3

To see a full list of what options are available, use the --help command.

java -jar project1.jar --help


-- Source Code -----
The source code for the project, as well as the SBT build file, are located in the source/ directory.

The scala sourc code is located in the source/src/main/scala/project1 directory


----- Size of Work Unit -----
The size of work unit I use is by default 10,000. I discovered this was an optimal size by simply measuring the performance of using different work unit values between 5,000 and 100,000. 10,000 ended up yielding the best performance.


----- Coins prefixed with 4 zeros -----
Output from calculating 10,000,000 hashes looking for coins prefixed with at least 4 zeros. 163 coins were found in total on this run, so I've shown an abridged version of the output here. The entire output is in the 4coins.txt file.

$ java -jar project1.jar master 4
bbokorneysYE=   0000e7e19c875a99f3b036cfc3190bc5fdf9197e447ca3796dbfc26f32f0f41e
bbokorneyAb/p   000011c0edeb01b1577fef168b6d843a856df12bd86ee7ed49b311dafad398c7
bbokorneyAZge   000010f967ee81e405394466249dbdea59a3c8d0844f954225aa30afb78ea28c
bbokorneyAjYT   0000d60a557fffe5140aeeb20265ad034dfa1651db13e8b9cd64b41e1e1b96b0
bbokorneyAxPR   0000616ba4cba1235bcc03c9a134cf10cbb6b33755688ced019a3f8860ee7802
...
bbokorneykXab   00000b1dbd6f22e1b0c262f919335157a105b0708c25389eef29c012772f1361
bbokorneyke2l   0000ef7dbd88acddd61f67553059a049d35052a38d770e7fa73ab5feb6278b06
bbokorneykpMG   000080879356e76167b131b19f91ce45f898d3fab470d9e2fc5be078c9b50bf6
bbokorneylGzZ   0000626916b35d2a265d0844d280b2964b80590e3eee0be9cd06f095ae626f8d
bbokorneylzbT   00005129e907dc78f2e58bf80336272efd399d7bb2a3c6e97fae8d04fd2454f2


----- Amount of Parallelsim -----
Measured output with the 'time' command from calculating 10,000,000 hashes on one machine with 8 cores and 8 worker actors. This output shows coins with hashes prefixed with at least 5 zeros.

$ time java -jar project1.jar master 5
bbokorneyD33i   00000f83e7762d5abdc279e5a52de167ab7366de82d24a3511bd3da8ada0018a
bbokorneyESxF   000002b8ab7e49f11b6c7214fad976647d0350d9e8237112857849e31967967d
bbokorneyGX/I   00000db1c85124bf0d2fb556219910046af144a87df856510dd4c2dd4252f758
bbokorneyJkG1   0000031e063647cf4c9fbbea072f626642ba6c8bf91c0ddaa2c7cadf7b6ba41d
bbokorneyLgX/   00000a4f9144ca24c5982492597777a51721b08f4c05ea3a2c309bf8d14431cb
bbokorneyNk6t   00000260563d4f06ea3dc4550f6ff8453783f9b64ac6f5b6d06faefa62326580
bbokorneyVAb/   000007598a2d9023ee275e3c236f260e6ce6baa32977e4386ad81ca524263b1a
bbokorneyXhz0   00000bc52fe51d89496f43829e494aea702d3265fcb9c5f1f8f304843a80c022
bbokorneyYZ/N   000006b41bdb9c6b863f8d640d74a9045ec41f41a6c9b70cb6459703a1c4de3b
bbokorneyaVdR   00000060b190d42e87195b2ddf62a4ec9cdff9132f0cfac9343db5ce2f8c3ee9
bbokorneyadld   000008ebdd8afffdd53568a9911871f27ac4c0416063acf55505058e7865c394
bbokorneydT9J   00000275455e9b25c63fafc4e2d3997a917a155540a7f91cfa498f3469a9b386
bbokorneye3K6   0000094e1e49c418c4e2daf53aafa448b5b7d606fa156e4faf3c2ca13e1a662a
bbokorneyijCw   00000012078fe7ad342df6d793bd72ac00767e55ce7863edfefc350e7dfd2001
bbokorneykXab   00000b1dbd6f22e1b0c262f919335157a105b0708c25389eef29c012772f1361
602.636u 6.258s 1:19.03 770.4%  0+0k 0+600io 0pf+0w

As shown by the output from the 'time' command, the ratio of CPU time to real time was 7.704, which is very close to the theoretical limit possible with 8 cores.


----- Coins Found -----
Over the 4 large runs I performed (see section below), I search 8 billion input values and yielded a total of 7673 coins (the coins are given in the file allCoins.txt). 

The coin with largest prefix was 8, and is show below.

bbokorneyAVGitD0=       00000000800f962afd3fd6620758c4554a7cd3070b428bac3d954ea13381d640

Of the 7673 coins I found, the prfixes are broken down as follows:

8 - 1 coin
7 - 26 coins
6 - 447 coins
5 - 7199 coins


----- Execution on Large Number of Machines -----
The largest number of machines I ran my program on simultaneously was 66 machines. To do this, I ran my program on every machine in rooms E113, E115, and E116 in the computer lab (I ensured that no one was using the machines when I did this).

I did a total of 4 runs with increasingly larger numbers of machines. For each run I search 2 billion values, looking for coins prefixed with 5 or more zeros. Some detials of each run are shown below.

Run 1 - 5 machines
24056.893u 173.411s 1:41:33.35 397.6% 0+0k 0+41112io 0pf+0w
2 billion / 6093.35 = 328,226.67 hashes/sec
1,930 coins found

Run 2 - 24 machines
953.252u 55.695s 14:34.66 115.3%      0+0k 112880+35088io 10pf+0w
2 billion hashes / 874.66 sec = 2,286,602.79 hashes/sec
1,870 coins found

Run 3 - 46 machines
571.305u 46.969s 7:21.52 140.0%       0+0k 0+32880io 0pf+0w
2 billion hashes / 441.52 sec = 4,529,806.12 hashes/sec
1,944 coins found

Run 4 - 66 machines
444.523u 42.217s 6:20.30 127.9%       0+0k 0+34088io 0pf+0w
2 billion hashes / 380.3 sec = 5,259,006.05 hashes/sec
1,929 coins found

The performance increased significantly from 5 to 24 machines. From 24 to 46 machines the performance increased again. From 46 to 66 machines, the performance increased only slightly. Adding more machines gave performance increases with diminishing returns.  I only used one master machine for each run, and I suspect a major reason for the diminishing returns was due to my master program not being able to handle the requests fast enough for that many worker machines. I would need to redesign the master, and probably distribute it as well, in order to accomodate that many machines. The master program would need to be able to scale properly with the larger number of workers.

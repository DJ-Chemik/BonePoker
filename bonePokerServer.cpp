#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <string>
#include <iostream>
#include <functional>
#include <thread>
#include <map>
#include <vector>
#include <set>
#include <memory>

#define MAX_CONNECTIONS 32
#define BUFFER_SIZE 4096

using namespace std;


int main()
{
    int fd, cfd, on=1;
    int port = 1234;
    struct sockaddr_in saddr, caddr;
    socklen_t l;
    fd=socket(PF_INET, SOCK_STREAM, 0);
    setsockopt(fd, SOL_SOCKET, SO_REUSEADDR, (char*)&on, sizeof(on));
    saddr.sin_family=PF_INET;
    saddr.sin_addr.s_addr = INADDR_ANY;
    saddr.sin_port=htons(port);

    bind(fd, (struct sockaddr*) &saddr, sizeof(saddr));
    listen(fd, 10);
    char buf[BUFFER_SIZE];

    map<string, string> mapa =
    {
        {"132336", "Kamil Wężniejewski\n"},
        {"136809", "Szymon Szczepański\n"}
    };

    vector<string> indeksy = {"136809", "132336"};;
    vector<string> nazwiska ={"Szymon Szczepański\n","Kamil Wężniejewski\n"};
    
    while(true)
    {
        l=sizeof(caddr);
        cfd=accept(fd, (struct sockaddr*) &caddr, &l);
        cout<<"Connection from "<<inet_ntoa(caddr.sin_addr)<<":"<<caddr.sin_port<<endl;
        int rc=read(cfd, buf, BUFFER_SIZE);

        /*for(int i=0;i<indeksy.size();i++)
        {
            if(strncmp(buf, (const char*)indeksy[i].c_str(), indeksy[i].size())==0)
            {
                write(cfd, (const char*)nazwiska[i].c_str(),nazwiska[i].size());
            }
            else
            {
                write(cfd,"Error\n",6);
            }  
        }*/

        if(strncmp(buf, "132336", 6)==0)
        {
            write(cfd, "Kamil Wężniejewski\n",sizeof("Kamil Wężniejewski\n"));
        }else if(strncmp(buf, "136809", 6)==0){
            write(cfd, "Szymon Szczepański\n",sizeof("Szymon Szczepański\n"));
        }else{
            write(cfd, "Błąd\n", 5);
        }  

        close(cfd);

    }
    close(fd);
    
    return 0;
}
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

/*
    Serwer musi odbierać info o wynikach i je porównywać
    Serwer otrzymuje od klienta 3 liczby z info o wynikach.
    Im większe liczby tym lepsze.
        
    #1 - jaka figura [0-8]
        -[0] - Nic
        -[1] - Para
        -[2] - Dwie Pary
        -[3] - Trójka
        -[4] - Mały Strit
        -[5] - Duży Strit
        -[6] - Full
        -[7] - Kareta
        -[8] - Poker

    #2 - jakie są liczby znaczące w danej wigurze (sprawdzane tylko gdy #1 są równe)
        -[0] - Nic [5-30]
                - suma wszystkich oczek
        -[1] - Para [1-6]
                - w zależności z jakich liczb składa się Para
        -[2] - Dwie Pary [2-12]
                - jedna para daje liczbe [1-6] i druga tez. Później sumujemy i suma jest liczbą #2
        -[3] - Trójka [1-6]
                - w zależności z jakich kości składa się Trójka
        -[4] - Mały Strit [0]
                ----  zawsze 0
        -[5] - Duży Strit [0]
                ---- zawsze 0
        -[6] - Full [1-6]
                - w zależności z jakich kości składa się Trójka w tym Fullu
        -[7] - Kareta [1-6]
                - w zależności z jakich kości składa się Kareta
        -[8] - Poker [1-6]
                - w zależności z jakich kości składa się Poker

    #3 - punkty za kości które nie wchodzą w skład figury (wyjątek Full) 
        (rzadko sprawdzane, tylko gdy poprzednie są równe)
        -[0] - Nic [0]
                ---- zawsze 0
        -[1] - Para [3-18]
                - suma liczby oczek na wolneych 3 kościach
        -[2] - Dwie Pary [1-6]
                - liczba oczek na wolnej kości
        -[3] - Trójka [2-12]
                - suma liczby oczek na wolneych 2 kościach
        -[4] - Mały Strit [0]
                ---- zawsze 0
        -[5] - Duży Strit [0]
                ---- zawsze 0
        -[6] - Full [1-6]
                - w zależności z jakich oczek składa się para w Fullu
        -[7] - Kareta [1-6]
                - liczba oczek na wolnej kości
        -[8] - Poker [0]
                ---- zawsze 0
*/

int main()
{
    int fd, cfd, on=1;
    int port = 1234;
    struct sockaddr_in server_addr, client_addr;
    socklen_t length;
    fd=socket(PF_INET, SOCK_STREAM, 0);
    setsockopt(fd, SOL_SOCKET, SO_REUSEADDR, (char*)&on, sizeof(on));
    server_addr.sin_family=PF_INET;
    server_addr.sin_addr.s_addr = INADDR_ANY;
    server_addr.sin_port=htons(port);

    bind(fd, (struct sockaddr*) &server_addr, sizeof(server_addr));
    listen(fd, 10);
    char buf[BUFFER_SIZE];

    vector<string> indeksy = {"136809", "132336"};;
    vector<string> nazwiska ={"Szymon Szczepański\n","Kamil Wężniejewski\n"};
    
    while(true)
    {
        length=sizeof(client_addr);
        cfd=accept(fd, (struct sockaddr*) &client_addr, &length);
        cout<<"Connection from "<<inet_ntoa(client_addr.sin_addr)<<":"<<client_addr.sin_port<<endl;
        int sizeReadData=read(cfd, buf, BUFFER_SIZE);
        int kod = atoi(buf);
        write(1,buf,sizeReadData);
        write(cfd, "Przesłano kod\n",sizeof("Przesłano kod\n"));
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
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
#include <sstream>
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

class Gracz{
public:
        int hash;
        int hash0=getCyfraFromLiczba(hash, 4);
        int hash1=getCyfraFromLiczba(hash, 3);
        int hash2=getCyfraFromLiczba(hash, 2);
        int hash3=getCyfraFromLiczba(hash, 1);
        bool czy_wygral = false;
        int liczba_punktow;
        
        
        int getCyfraFromLiczba(int liczba, int nrCyfryOdKonca)
        {

        int x;
        if(nrCyfryOdKonca==1){
                return liczba%10;
        }else if(nrCyfryOdKonca==2){
                x=(liczba%100);
                return x/10;
        }else if(nrCyfryOdKonca==3){
                x=(liczba%1000);
                return x/100;
        }else if(nrCyfryOdKonca==4){
                x=(liczba%10000);
                return x/1000;
        }else if(nrCyfryOdKonca==5){
                x=(liczba%100000);
                return x/10000;
        }else if(nrCyfryOdKonca==6){
                x=(liczba%1000000);
                return x/100000;
        }else{
                return -1;
        }
        }

        /**
         * -3punkty za zwycięztwo, 0 za przegraną
         * -każda figura ma określony nr i przyznawane są punkty za tą figurę równą jej numerowi
         */
        int obliczWynikGracza(){
                int punkty = 0;
                if (czy_wygral==true)
                {
                        punkty+=3;
                }
                punkty+=hash1;
                liczba_punktow=punkty;
                return punkty;                
        }

};



char* sendTo(int deskryptor, int hashToSend, int length){
    stringstream ss;
    
    ss.str("");
    ss<<hashToSend;
    string temp = ss.str();
    char* charToSend = (char*)temp.c_str();
        
    write(deskryptor,charToSend,length);
    return charToSend;
}



//1 - gdy wygrywa gracz 1, 2 - gdy wygrywa gracz 2, 0 - gdy remis, -1 gdy błąd
int porownajWynikiGraczy(Gracz gracz1, Gracz gracz2){
  
    if(gracz1.hash0==0 && gracz2.hash0==0){
        if(gracz1.hash1>gracz2.hash1){
                gracz1.czy_wygral=true;
                return 1;
        }else if (gracz1.hash1<gracz2.hash1){
                gracz2.czy_wygral=true;
                return 2;
        }else
        {
                if(gracz1.hash2>gracz2.hash2){
                        gracz1.czy_wygral=true;
                        return 1;
                }else if (gracz1.hash2<gracz2.hash2){
                        gracz2.czy_wygral=true;
                        return 2;  
                }else{
                        if(gracz1.hash3>gracz2.hash3){
                                gracz1.czy_wygral=true;
                                return 1;
                         }else if (gracz1.hash3<gracz2.hash3){
                                gracz2.czy_wygral=true;
                                return 2;
                        }else{
                              return 0;  
                        }
                }
        }
      
    }else{
        return -1;
    }
}

int main()
{
    int fd, cfd1, cfd2, on=1;
    int port = 1234;
    struct sockaddr_in server_addr, client1_addr;
    socklen_t length;
    fd=socket(PF_INET, SOCK_STREAM, 0);
    setsockopt(fd, SOL_SOCKET, SO_REUSEADDR, (char*)&on, sizeof(on));
    server_addr.sin_family=PF_INET;
    server_addr.sin_addr.s_addr = INADDR_ANY;
    server_addr.sin_port=htons(port);
    bind(fd, (struct sockaddr*) &server_addr, sizeof(server_addr));
    listen(fd, 10);
    Gracz gracz1, gracz2;
    
    while(true)
    {
        char buf[BUFFER_SIZE];
        length=sizeof(client1_addr);
        cfd1=accept(fd, (struct sockaddr*) &client1_addr, &length);
        cout<<"Connection from "<<inet_ntoa(client1_addr.sin_addr)<<":"<<client1_addr.sin_port<<endl;
        int sizeReadData=read(cfd1, buf, BUFFER_SIZE);
        
        gracz1.hash = atoi(buf);
        //write(1,buf ,sizeReadData);
        cout<<gracz1.obliczWynikGracza()<<endl;
        sendTo(cfd1, gracz1.hash,4);

        //if(strncmp(buf, "123", 3)==0)
        

        close(cfd1);
        memset(buf, 0, sizeof(buf));

    }
    close(fd);
    
    return 0;
}

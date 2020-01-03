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


#define HASH_SIGNAL_FROM_PLAYER_RESULT 1 //pierwsza cyfra w Hashu od gracza informująca że przesyła on wynik
//#define HASH_SIGNAL_FROM_PLAYER_CONFIGURE 9 //pierwsza cyfra w Hashu od gracza informująca że przesyła on nie wynik a dane konfiguracyjne

//Connection
#define HASH_CONNECT 911111 //klient pyta czy może dołaczyć
#define HASH_C1_IS_STILL_WAIT 910011 //klient 1 pyta czy nadal czekać
#define HASH_C2_IS_STILL_WAIT 920022 //klient 2 pyta czy nadal czekać
#define HASH_PLAYER_NUMBER_1 910000 //serwer wysyła do klienta info że jest graczem pierwszym
#define HASH_PLAYER_NUMBER_2 920000 //serwer wysyła do klienta info że jest graczem drugim
#define HASH_WAIT_FOR_OPPONENT 990000 //serwer wysyła informacje żeby gracz 1 czekał na podłączenie gracza 2
#define HASH_OPPONENT_IS_READY 991111 //serwer wysyła informacje do gracza 1 że gracz 2 sie juz podłączył do gry

//Game
#define HASH0_PLAYER_1_RESULT 1 //tak zaczyna się hash gdy klient 1 lub serwer do niego przysyła wyniki
#define HASH0_PLAYER_2_RESULT 2 //tak zaczyna się hash gdy klient 2 lub serwer do niego przysyła wyniki

//End
#define HASH_END_GAME 999999 //Zakończenie gry



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

        int hash;
public:
        int hash0;
        int hash1;
        int hash2a;
        int hash2b;
        int hash3a;
        int hash3b;
        bool czy_wygral = false;
        int liczba_punktow;

        Gracz(){
                hash=0;
                hash0=0;
                hash1=0;
                hash2a=0;
                hash2b=0;
                hash3a=0;        
                hash3b=0;
        }
        void resetGracz(){
                setHash(0);
                czy_wygral=0;
                liczba_punktow=0;
        }

        void setHash(int newHash){
                hash=newHash;              
                hash0=getCyfraFromLiczba(hash, 6);
                hash1=getCyfraFromLiczba(hash, 5);
                hash2a=getCyfraFromLiczba(hash, 4);
                hash2b=getCyfraFromLiczba(hash, 3);
                hash3a=getCyfraFromLiczba(hash, 2);        
                hash3b=getCyfraFromLiczba(hash, 1);
        }

        int getHash(){
                return hash;
        }
 
        int getCyfraFromLiczba(int liczba, int nrCyfryOdKonca){

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

class Game{
        int numerEtapu = 0;        //0 -podłączanie, 1-6 rozgrywka, 7 - zakończenie
        int iloscGraczyPodlaczonych=0;
        int iloscGraczyKtorzyPrzeslaliWyniki=0;
        
public:
        Gracz gracz1;
        Gracz gracz2;

        void resetGame(){
                numerEtapu=0;
                iloscGraczyPodlaczonych=0;
                iloscGraczyKtorzyPrzeslaliWyniki=0;
                gracz1.resetGracz();
                gracz2.resetGracz();
        }

        void incrementEtap(){
                if (numerEtapu<7)
                {
                        numerEtapu++;
                }else
                {
                        numerEtapu=0;
                }
                
                
        }
        int setNumerEtapu(int numer){
                if (numer>=0 && numer<=7)
                {
                        numerEtapu=numer;
                        return 0;
                }else{
                        return -1;
                }
                
        }
        int getNumerEtapu(){
                return numerEtapu;
        }
        int getLiczbaGraczy(){
                return iloscGraczyPodlaczonych;
        }
        void setLiczbaGraczy(int liczba){
                iloscGraczyPodlaczonych=liczba;
        }
        int getLiczbaPrzeslanychWynikow(){
                return iloscGraczyKtorzyPrzeslaliWyniki;
        }
        void setLiczbaOtrzymanychWynikow(int liczba){
                iloscGraczyKtorzyPrzeslaliWyniki=liczba;
        }

        //1 - gdy wygrywa gracz 1, 2 - gdy wygrywa gracz 2, 0 - gdy remis, -1 gdy błąd
        int porownajWynikiGraczy(){
        
                if(gracz1.hash0==HASH0_PLAYER_1_RESULT && 
                gracz2.hash0==HASH0_PLAYER_2_RESULT)
                {
                        if(gracz1.hash1>gracz2.hash1){
                                gracz1.czy_wygral=true;
                                return 1;
                        }else if (gracz1.hash1<gracz2.hash1){
                                gracz2.czy_wygral=true;
                                return 2;
                        }else
                        {
                                if(gracz1.hash2a>gracz2.hash2a){
                                        gracz1.czy_wygral=true;
                                        return 1;
                                }else if (gracz1.hash2a<gracz2.hash2a){
                                        gracz2.czy_wygral=true;
                                        return 2;  
                                }else{
                                        if(gracz1.hash2b>gracz2.hash2b){
                                                gracz1.czy_wygral=true;
                                                return 1;
                                        }else if (gracz1.hash2b<gracz2.hash2b){
                                                gracz2.czy_wygral=true;
                                                return 2;
                                        }else{
                                                if(gracz1.hash3a>gracz2.hash3a){
                                                        gracz1.czy_wygral=true;
                                                        return 1;
                                                }else if (gracz1.hash3a<gracz2.hash3a){
                                                        gracz2.czy_wygral=true;
                                                        return 2;
                                                }else{
                                                
                                                        if(gracz1.hash3b>gracz2.hash3b){
                                                                gracz1.czy_wygral=true;
                                                                return 1;
                                                        }else if (gracz1.hash3b<gracz2.hash3b){
                                                                gracz2.czy_wygral=true;
                                                                return 2;
                                                        }else{
                                                                return 0;
                                                        }
                                                }
                                        }
                                }
                        }
                
                }else{
                        return -1;
                }
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

int analyzeConnectionPart(int receivedHash, Game game){
        if (receivedHash==HASH_CONNECT)
                {
                        if (game.getLiczbaGraczy()==0)
                        {
                                game.setLiczbaGraczy(1);
                                return HASH_PLAYER_NUMBER_1;
                        }
                        if (game.getLiczbaGraczy()==1)
                        {
                                game.setLiczbaGraczy(2);
                                return HASH_PLAYER_NUMBER_2;
                        }
                        
                }
                if (receivedHash==HASH_C1_IS_STILL_WAIT)
                {
                        if (game.getLiczbaGraczy()==1)
                        {
                                
                                return HASH_WAIT_FOR_OPPONENT;
                        }
                        if (game.getLiczbaGraczy()==2)
                        {
                                game.incrementEtap();
                                return HASH_OPPONENT_IS_READY;
                        }
                }
        return -1; //ERROR gdy żadna z powyższych ścieżek się nie powiedzie    
}

int analyzeGamingPart(int receivedHash, Game game){
        int whichPlayer = game.gracz1.hash0;

                if (receivedHash==HASH_C1_IS_STILL_WAIT){
                        if (game.getLiczbaPrzeslanychWynikow()==1)
                        {       
                                return HASH_WAIT_FOR_OPPONENT;
                        }
                        if (game.getLiczbaPrzeslanychWynikow()==2)
                        {
                                game.gracz1.czy_wygral=false;
                                game.gracz2.czy_wygral=false;
                                //zwraca wynik
                                return whichPlayer*100000+game.gracz1.liczba_punktow*100+game.gracz2.liczba_punktow;
                        }
                }else if(receivedHash==HASH_C2_IS_STILL_WAIT){
                        if (game.getLiczbaPrzeslanychWynikow()==1)
                        {        
                                return HASH_WAIT_FOR_OPPONENT;
                        }
                        if (game.getLiczbaPrzeslanychWynikow()==2)
                        {
                                game.gracz1.czy_wygral=false;
                                game.gracz2.czy_wygral=false;
                                game.incrementEtap();
                                //zwraca wynik
                                return whichPlayer*100000+game.gracz2.liczba_punktow*100+game.gracz1.liczba_punktow;       
                        }
                }else{
                        if (game.getLiczbaPrzeslanychWynikow()==0)
                        {        
                                if (whichPlayer==1)
                                {
                                        game.gracz1.setHash(receivedHash);
                                }
                                if (whichPlayer==2)
                                {
                                        game.gracz2.setHash(receivedHash);
                                }
                                game.setLiczbaOtrzymanychWynikow(1);
                                return HASH_WAIT_FOR_OPPONENT;
                        }
                        if (game.getLiczbaPrzeslanychWynikow()==1)
                        {
                                if (whichPlayer==1)
                                {
                                        game.gracz1.setHash(receivedHash);
                                }
                                if (whichPlayer==2)
                                {
                                        game.gracz2.setHash(receivedHash);
                                }
                                game.porownajWynikiGraczy();
                                game.gracz1.obliczWynikGracza();
                                game.gracz2.obliczWynikGracza();
                                game.setLiczbaOtrzymanychWynikow(2);
                                if (whichPlayer==1)
                                {
                                        return whichPlayer*100000+game.gracz1.liczba_punktow*100+game.gracz2.liczba_punktow;
                                }
                                if (whichPlayer==2)
                                {
                                        return whichPlayer*100000+game.gracz2.liczba_punktow*100+game.gracz1.liczba_punktow;
                                }
                        }            
                }
        return -1; //ERROR gdy żadna z powyższych ścieżek się nie powiedzie    
}

int analyzeEndingPart(int receivedHash, Game game){
        if (receivedHash==HASH_END_GAME)
        {
                game.resetGame();
        }
        return -1; //ERROR gdy żadna z powyższych ścieżek się nie powiedzie  
}

int analyzeReceivedHash(int receivedHash, Game game){
        if (game.getNumerEtapu()==0){
                return analyzeConnectionPart(receivedHash, game);             

        }else if (game.getNumerEtapu()==1){
                return analyzeGamingPart(receivedHash,game);

        }else if (game.getNumerEtapu()==2){
                return analyzeGamingPart(receivedHash,game);
        }else if (game.getNumerEtapu()==3){
                return analyzeGamingPart(receivedHash,game);
        }else if (game.getNumerEtapu()==4){
                return analyzeGamingPart(receivedHash,game);
        }else if (game.getNumerEtapu()==5){
                return analyzeGamingPart(receivedHash,game);
        }else if (game.getNumerEtapu()==6){
                return analyzeGamingPart(receivedHash,game);
        }else if (game.getNumerEtapu()==7){
                return analyzeEndingPart(receivedHash,game);
        }
             
        return -1; //ERROR gdy żadna z powyższych ścieżek się nie powiedzie    
}


int main()
{      
    int fd, on=1;
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
    Game game;

    fd_set rmask;
    FD_ZERO(&rmask);

    fd_set wmask;
    FD_ZERO(&wmask);

    int fdmax = fd;
    int sizeReadData;
    
    FD_SET(fd,&rmask);
    static struct timeval timeout;
    timeout.tv_sec = 5 * 60;
    timeout.tv_usec = 0;
    
    while(true)
    {
        
        char buf[BUFFER_SIZE];
        int cfd1;
        int respondHash;

        int wynikSelect = select(fdmax + 1, &rmask, &wmask, (fd_set *)0, &timeout);
        if (wynikSelect == 0)
        {
            printf("timed out\n");
            continue;
        }

        int ileZostaloDeskryptorowDoObslugi=wynikSelect;
        if (FD_ISSET(fd, &rmask))
        {
                //ileZostaloDeskryptorowDoObslugi-=1;
                length=sizeof(client1_addr);
                cfd1=accept(fd, (struct sockaddr*) &client1_addr, &length);
                cout<<"Connection from : "<<inet_ntoa(client1_addr.sin_addr)<<":"<<client1_addr.sin_port<<endl; 
                FD_SET(cfd1, &rmask);

                if (cfd1>fdmax)
                {
                        fdmax=cfd1;
                }
                  
        }
        for (int i = fd+1; i <= fdmax && ileZostaloDeskryptorowDoObslugi>0; i++)
        {
                if (FD_ISSET(i, &rmask))
                {
                        ileZostaloDeskryptorowDoObslugi-=1;
                        sizeReadData=read(i, buf, BUFFER_SIZE);
                        if(i==cfd1)
                        {
                                //game.gracz1.setHash(atoi(buf));
                                cout<<"Otrzymany hash :"<<game.gracz1.getHash()<<endl;
                                int receivedHash=atoi(buf);
                                cout<<"Otrzymany hash :"<<receivedHash<<endl;
                                respondHash=analyzeReceivedHash(receivedHash,game);
                                
                        }                       
                        
                        FD_CLR(i, &rmask);
                        FD_SET(i, &wmask);
                }

                if (FD_ISSET(i, &wmask))
                {
                        ileZostaloDeskryptorowDoObslugi-=1;
                        
                        int hashToSend = respondHash;
                        if(i==cfd1)
                        {
                                sendTo(i, hashToSend,6);
                                cout<<"Wysłany hash: "<<hashToSend<<endl;
                        }

                        close(i);
                        FD_CLR(i, &wmask);

                        if (i == fdmax)
                        {
                          while (fdmax > fd && !FD_ISSET(fdmax, &wmask))
                          {
                            fdmax -= 1;
                          }
                        }
                }      
        }       

        //close(cfd1);
        //memset(buf, 0, sizeof(buf));

    }
    close(fd);
    
    return 0;
}

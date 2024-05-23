#include <windows.h>
#include <stdio.h>
#include <string.h>
#include <iostream>
#include <limits>
#include <fstream>
#include <future>
#include <chrono>

#define SOH 0x1
#define EOT 0x4
#define ACK 0x6
#define NAK 0x15

#define C 'C'
#define SUB 26

using namespace std;

////////////////// ZMIENNE GLOBALNE SYSTEMOWE //////////////////////
HANDLE handleCom;
BOOL isReadyPort;
DCB controlDCB;
COMMTIMEOUTS objCommtime;

//JESLI BEDZIE NIE DZIALAC TO ODKOMNETOWAC


int parzystosc;
int BaudRate; //= 57600;
// int BaudRate = 9600;

bool crc;
char *COM;
const char *file;

//////////////////// FUNKCJE ///////////////////////
template<typename Type>
void isCorrect(Type &variable) {
    while ((!(cin >> variable))) {
        cin.clear();
        cin.ignore(numeric_limits<streamsize>::max(), '\n');
        cout << "Wrong date, enter again: ";
    }
    cin.ignore(numeric_limits<streamsize>::max(), '\n');
}

//FUNKCJA ODPOWIEDZIALNA ZA INICJALIZACJE PORTU
void initialize(char *chosenPort) {
    handleCom = CreateFile(chosenPort, GENERIC_READ | GENERIC_WRITE, 0, NULL, OPEN_EXISTING, 0, NULL);
    isReadyPort = SetupComm(handleCom, 1, 128); // USTAWIANIE WIELKOSCI BUFORA
    isReadyPort = GetCommState(handleCom, &controlDCB);
    controlDCB.BaudRate = BaudRate;
    controlDCB.ByteSize = 8;
    controlDCB.Parity = parzystosc;  NOPARITY;EVENPARITY;
    controlDCB.StopBits = ONESTOPBIT;
    controlDCB.fAbortOnError = TRUE;
    controlDCB.fOutX = FALSE; // XON/XOFF WYLACZANIE DO TRANSMISJI
    controlDCB.fInX = FALSE; // XON/XOFF WYLACZANIE DO ODBIERANIA
    controlDCB.fOutxCtsFlow = FALSE; // WLACZANIE CTS flow control
    controlDCB.fRtsControl = RTS_CONTROL_HANDSHAKE;
    controlDCB.fOutxDsrFlow = FALSE; //WLACZENIE DSR FLOW CONTROL
    controlDCB.fDtrControl = DTR_CONTROL_ENABLE;
    controlDCB.fDtrControl = DTR_CONTROL_DISABLE;
    controlDCB.fDtrControl = DTR_CONTROL_HANDSHAKE;
    isReadyPort = SetCommState(handleCom, &controlDCB);
}

//FUNKCJA ODPOWIEDZIALNA ZA ODCZYTYWANIE DANYCH Z PLIKU
void receiveCOM(unsigned char *sign, int length) {
    DWORD pos = 0, num;
    while (pos < length) {
        ReadFile(handleCom, sign + pos, length - pos, &num, NULL);
        pos += num;
    }
}


bool receiveCOM2(unsigned char *sign, int length) {
    DWORD pos = 0, num;
    while (pos < length) {
        ReadFile(handleCom, sign + pos, length - pos, &num, NULL);
        pos += num;
    }
    return true;
}


//FUNKCJA ODPOWIEDZIALNA ZA ZAPISYWANIE DANYCH DO PLIKU
void sendCOM(unsigned char *sign, int length) {
    DWORD num;
    WriteFile(handleCom, sign, length, &num, NULL);
}

//FUNKCJA ODPOWIEDZIALNA ZA CRC WERSJA 16 - WIELOMIAN 17 BITOWY
short int CRC16(unsigned char *fileBuffer) {
    int tmp = 0, val = 0x18005 << 15;
    for (int i = 0; i < 3; i++) {
        tmp = tmp * 256 + (unsigned char) fileBuffer[i];
    }
    tmp *= 256;

    for (int i = 3; i < 134; i++) {
        if (i < 128) {
            tmp += (unsigned char) fileBuffer[i];
        }
        for (int j = 0; j < 8; j++) {
            if (tmp & (1 << 31)) {
                tmp ^= val;
            }
            tmp <<= 1;
        }
    }
    return tmp >> 16;
}

/**
 * @brief This function is responsible for receiving data. It uses the sendCOM() and receiveCOM() functions.
 */
void receiving() {
    unsigned char buf[3], fileBuffer[128]; // Buffer for received data
    //initialize(COM);

    buf[0] = crc ? C : NAK; // Send NAK signal
    sendCOM(buf, 1);

    // Open a file in binary write mode, overwriting any existing content
    FILE *a = fopen(".a.txt", "wb"); //OTWIERANIE TRYB WB - TRYB BINARNY ORAZ NADPISYWANIE(KASUJE STARA ZAWARTOSC)
    while (true) {
        unsigned short sum, sumc;
        receiveCOM(buf, 3); // Receive block header
        receiveCOM(fileBuffer, 128); // Receive block

        sum = sumc = 0;
        receiveCOM((unsigned char *) &sum, crc ? 2 : 1);

        if (crc) {
            sumc = CRC16(fileBuffer);
        } else {
            for (int i = 0; i < 128; i++) {
                sumc += (unsigned char) fileBuffer[i];
            }
            sumc %= 256;
        }

        if (sum != sumc) {
            buf[0] = NAK;
            sendCOM(buf, 1);
            continue;
        }

        buf[0] = ACK;
        sendCOM(buf, 1);

        receiveCOM(buf, 1);
        if (buf[0] == EOT) {
            unsigned char last = 127;
            while (fileBuffer[last] == SUB) {
                last--;
            }
            fwrite(fileBuffer, 1, last + 1, a);
            fclose(a);
            buf[0] = ACK;
            sendCOM(buf, 1);
            break;
        }
        fwrite(fileBuffer, 128, 1, a);
        fclose(a);
        buf[0] = ACK;
        sendCOM(buf, 1);
    }


    std::string file; // Zmienna, do której będziemy zapisywać nazwę pliku odczytaną z pliku tekstowego

    // Otwórz strumień wejściowy do odczytu pliku "a.txt"
    std::ifstream inputFile(".a.txt");

    // Sprawdź, czy plik został otwarty poprawnie
    if (!inputFile.is_open()) {
        std::cerr << "Nie można otworzyć pliku do odczytu." << std::endl;
    }

    // Odczytaj zawartość pliku i zapisz ją do zmiennej file
    inputFile >> file;

    // Zamknij plik
    inputFile.close();


    FILE *f = fopen(file.c_str(), "wb"); //OTWIERANIE TRYB WB - TRYB BINARNY ORAZ NADPISYWANIE(KASUJE STARA ZAWARTOSC)


    auto timeout = std::chrono::seconds(1);


    while (true) {
        std::future<bool> resultFuture = std::async(std::launch::async, receiveCOM2, buf, 1);

        std::future_status status = resultFuture.wait_for(timeout);

        if (status == std::future_status::ready) {
            // Wynik jest dostępny w określonym czasie
            bool result = resultFuture.get();
            if (result) {
                std::cout << "Rozpoczeto \"pobieranie\"!" << std::endl;
                break;
            }
        } else if (status == std::future_status::timeout) {
            std::cout << "Timeout!" << std::endl;
            if (resultFuture.valid()) {
                resultFuture.wait(); // Czekamy, aż zadanie zakończy się
            }
            std::cout << "Zadanie przerwane" << std::endl;
            break;
        }
    }

    // receiveCOM(buf, 1);                 //ODBIERANIE DANYCH
    //printf("%d\n", buf[0]);
    while (true) {
        unsigned short sum, sumc;
        receiveCOM(buf + 1, 2);
        receiveCOM(fileBuffer, 128);

        sum = sumc = 0;
        receiveCOM((unsigned char *) &sum, crc ? 2 : 1);

        if (crc) {
            sumc = CRC16(fileBuffer);
        } else {
            for (int i = 0; i < 128; i++) {
                sumc += (unsigned char) fileBuffer[i];
            }
            sumc %= 256;
        }

        //printf("%x %x \n", sum, sumc);
        if (sum != sumc) {
            buf[0] = NAK;
            sendCOM(buf, 1);
            continue;
        }

        buf[0] = ACK;
        sendCOM(buf, 1);

        receiveCOM(buf, 1);
        if (buf[0] == EOT) {
            unsigned char last = 127;
            while (fileBuffer[last] == SUB) {
                last--;
            }
            fwrite(fileBuffer, last + 1, 1, f);
            break;
        }
        cout << "Otrzymano blok nr " << (int) buf[1] << endl;
        fwrite(fileBuffer, 128, 1, f);
    }
    fclose(f);
    buf[0] = ACK;
    sendCOM(buf, 1);
}

const char *getFileName(const char *fullPath) {
    // Konwersja do std::string dla wygody manipulacji łańcuchami znaków
    std::string path(fullPath);

    // Znajdź ostatni separator ścieżki
    size_t pos = path.find_last_of("/\\");

    // Zwróć nazwę pliku, pomijając ścieżkę
    if (pos != std::string::npos) {
        return path.substr(pos + 1).c_str();
    }

    // Jeśli nie ma separatorów, zwróć pełną ścieżkę
    return fullPath;
}


//FUNKCJA ODPOWIEDZIALNA ZA ODBIERANIE DANYCH - KORZYSTA Z FUNKCJI sendCOM() oraz receivingCOM()
void sending() {
    unsigned char buf[3], fileBuffer[128];
    //initialize(COM);

    receiveCOM(buf, 1);
    //printf("%d\n", buf[0]);
    if (buf[0] == NAK) {
        crc = false;
    } else if (buf[0] == C) {
        crc = true;
    } else {
        return;
    }

    int no = 1;

    // Otwórz strumień wyjściowy do zapisu do pliku "a.txt"
    std::ofstream outputFile(".a.txt");

    // Sprawdź, czy plik został otwarty poprawnie
    if (!outputFile.is_open()) {
        std::cerr << "Nie można otworzyć pliku do zapisu." << std::endl;
    }

    // Zapisz nazwę pliku przechowywaną w zmiennej file do pliku tekstowego
    outputFile << getFileName(file);

    // Zamknij plik
    outputFile.close();

    FILE *a = fopen(".a.txt", "rb");


    fseek(a, 0, SEEK_END);

    int fsize = ftell(a);


    fseek(a, 0, SEEK_SET);
    while (ftell(a) < fsize) {
        unsigned char length = fread(fileBuffer, 1, 128, a);
        for (int i = length; i < 128; i++) {
            fileBuffer[i] = SUB;
        }
        unsigned short sum = 0;

        sum = 0;

        if (crc) {
            sum = CRC16(fileBuffer);
        } else {
            for (int i = 0; i < 128; i++)
                sum += (unsigned char) fileBuffer[i];
            sum %= 256;
        }

        buf[0] = SOH;
        buf[1] = no;
        buf[2] = 255 - no;

        sendCOM(buf, 3);
        sendCOM(fileBuffer, 128);
        sendCOM((unsigned char *) &sum, crc ? 2 : 1);

        receiveCOM(buf, 1);
        if (buf[0] == ACK) {
            no++;
        } else {
            fseek(a, -128, SEEK_CUR);
            ftell(a);
        }
    }

    fclose(a);
    do {
        buf[0] = EOT;
        sendCOM(buf, 1);
        receiveCOM(buf, 1);
    } while (buf[0] != ACK);


    no = 1;

    FILE *f = fopen(file, "rb");


    fseek(f, 0, SEEK_END);

    fsize = ftell(f);


    fseek(f, 0, SEEK_SET);
    while (ftell(f) < fsize) {
        unsigned char length = fread(fileBuffer, 1, 128, f);
        for (int i = length; i < 128; i++) {
            fileBuffer[i] = SUB;
        }
        unsigned short sum = 0;

        sum = 0;

        if (crc) {
            sum = CRC16(fileBuffer);
        } else {
            for (int i = 0; i < 128; i++)
                sum += (unsigned char) fileBuffer[i];
            sum %= 256;
        }

        buf[0] = SOH;
        buf[1] = no;
        buf[2] = 255 - no;

        cout << "Wysylanie bloku nr " << (int) (unsigned char) no << endl;;
        sendCOM(buf, 3);
        sendCOM(fileBuffer, 128);
        sendCOM((unsigned char *) &sum, crc ? 2 : 1);

        receiveCOM(buf, 1);
        if (buf[0] == ACK) {
            no++;
        } else {
            //printf("Retransmisja");
            fseek(f, -128, SEEK_CUR);
        }
    }

    fclose(f);
    do {
        buf[0] = EOT;
        sendCOM(buf, 1);
        receiveCOM(buf, 1);
    } while (buf[0] != ACK);
}


std::string openFileDialog() {
    OPENFILENAME ofn;
    char szFileName[MAX_PATH] = "";

    ZeroMemory(&ofn, sizeof(ofn));
    ofn.lStructSize = sizeof(ofn);
    ofn.lpstrFilter = "All Files\0*.*\0";
    ofn.lpstrFile = szFileName;
    ofn.nMaxFile = sizeof(szFileName);
    ofn.Flags = OFN_EXPLORER | OFN_FILEMUSTEXIST | OFN_PATHMUSTEXIST;

    if (GetOpenFileName(&ofn)) {
        return std::string(ofn.lpstrFile);
    } else {
        return ""; // Pusty string w przypadku, gdy użytkownik anuluje wybór pliku
    }
}


int main() {
    std::string filePath;
    cout << "Autorzy: Franciszek Pietrzycki & Jan Rzepkowski\n";


    int wyb_parz;
    do {
        cout << "Wybierz tryb pracy:"
                "\n1. Przystosc"
                "\n2. Nieparzystosc"
                "\nWybor: ";
        isCorrect(wyb_parz);
    } while (!(wyb_parz == 1 || wyb_parz == 2));

    if (wyb_parz == 1) {
        parzystosc = EVENPARITY;
    } else if (wyb_parz == 2) {
        parzystosc = NOPARITY;
    }

    int choice_of_bound_rate;
    do {
        cout << "Wybierz szybkosc transmisji: "
                "\n1. 9600 b/s"
                "\n2. 19200 b/s"
                "\n3. 38400 b/s"
                "\n4. 57600 b/s"
                "\n5. 460800 b/s"
                "\nWybor: ";
        isCorrect(choice_of_bound_rate);
    } while (!(choice_of_bound_rate == 1 || choice_of_bound_rate == 2 || choice_of_bound_rate == 3 ||
               choice_of_bound_rate == 4 || choice_of_bound_rate == 5));

    if (choice_of_bound_rate == 1) {
        BaudRate = 9600;
    } else if (choice_of_bound_rate == 2) {
        BaudRate = 19200;
    } else if (choice_of_bound_rate == 3) {
        BaudRate = 38400;
    } else if (choice_of_bound_rate == 4) {
        BaudRate = 57600;
    } else if (choice_of_bound_rate == 5) {
        BaudRate = 460800;
    }


    int choice;
    do {
        cout << "Wybierz tryb pracy:"
                "\n1. Wysylanie"
                "\n2. Odbieranie"
                "\nWybor: ";
        isCorrect(choice);
    } while (!(choice == 1 || choice == 2));


    int portNumber;
    do {
        cout << "Wybierz port: \n"
             << "1. COM1\n"
             << "2. COM2\n"
             << "3. COM3\n"
             << "4. COM4\n"
             << "5. COM5\n"
             << "6. COM6\n"
             << "7. COM7\n"
             << "8. COM8\n"
             << "Wybor: ";

        isCorrect(portNumber);
    } while (!(portNumber == 1 || portNumber == 2 || portNumber == 3 || portNumber == 4 || portNumber == 5 || portNumber
                                                                                                              == 6 || portNumber == 7 || portNumber == 8));

    switch (portNumber) {
        case 1: {
            COM = "COM1";
            break;
        }
        case 2: {
            COM = "COM2";
            break;
        }
        case 3: {
            COM = "COM3";
            break;
        }
        case 4: {
            COM = "COM4";
            break;
        }
        case 5: {
            COM = "COM5";
            break;
        }
        case 6: {
            COM = "COM6";
            break;
        }
        case 7: {
            COM = "COM7";
            break;
        }
        case 8: {
            COM = "COM8";
            break;
        }
    }
    initialize(COM);


    int CRCEnable;
    do {
        cout << "Wlaczyc CRC: "
                "\n1. Tak"
                "\n2. Nie"
                "\nWybor: ";
        isCorrect(CRCEnable);
    } while (!(CRCEnable == 1 || CRCEnable == 2));

    if (CRCEnable == 1) {
        crc = true;
    } else if (CRCEnable == 2) {
        crc = false;
    }

    switch (choice) {
        case 1: {
            string a = openFileDialog();
            cout << a;
            file = a.c_str();
            filePath = file;
            if (!filePath.empty()) {
                std::cout << "Wybrany plik: " << filePath << std::endl;
            } else {
                std::cout << "Nie wybrano zadnego pliku." << std::endl;
            }
            sending();
            break;
        }
        case 2: {
            receiving();
            break;
        }
    }
    return 0;
}
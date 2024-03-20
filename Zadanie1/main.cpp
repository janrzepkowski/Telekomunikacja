#include "include/koder.h"
#include "include/dekoder.h"
#include "include/odbiornik.h"
#include <fstream>
#include <iostream>
#include <vector>

using namespace std;

#define BEFORE_TRANSMISSION "../dane/wyslane_zakodowane.txt"
#define AFTER_TRANSMISSION "../dane/odebrane_zakodowane.txt"

int main() {

    cout << "Autorzy: Franek Pietrzycki Jan Rzepkowski\n";

    while (true) {
        cout << "Wprowadz 1, 2 lub 3: \n"
                "1. zapisz wiadomosc do pliku wiadomosc.txt\n"
                "2. zakoduj wiadomosc z pliku wiadomosc.txt\n"
                "3. odkoduj wiadomosc z pliku odebrane_zakodowane.txt w konsoli\n"
                "4. zakoncz dzialanie programu\n";
        int wybor;
        cin >> wybor;

        if (wybor == 1) {
            cout << "Podaj wiadomosc do zapisania \n";
            string message;
            cin >> message;
            ofstream plik;
            string wiadomoscZPliku;
            plik.open("../dane/wiadomosc.txt");
            plik << message;
            if (plik.is_open()) {
                plik.close();
            }
        } else if (wybor == 2) {
            ifstream plik;
            string wiadomoscZPliku;
            plik.open("../dane/wiadomosc.txt");
            if (plik.is_open()) {
                getline(plik, wiadomoscZPliku);
                plik.close();
            }
            Wiadomosc wiadomosc(wiadomoscZPliku);
            Koder kodownik(wiadomosc);
            ofstream wyjscie(BEFORE_TRANSMISSION);
            ofstream odebrane(AFTER_TRANSMISSION);
            for (const auto& slowo : kodownik.zakoduj()) {
                for (const auto& bit : slowo) {
                    wyjscie << bit;
                    odebrane << bit;
                }
            }
            wyjscie.close();
            odebrane.close();
            cout << "Wiadomosc: " + wiadomosc.pobierzWiadomosc() << "\n";
            cout << "Zakodowana wiadomosc: " + kodownik.pobierzZakodowanaWiadomosc() << "\n";
            cout << "Zapisano do pliku odebrane_zakodowane.txt\n";
        } else if (wybor == 3) {
            ifstream wejscie(AFTER_TRANSMISSION);
            string slowo;
            vector<vector<bool>> kodZPliku;
            while (!wejscie.eof()) {
                getline(wejscie, slowo);
                int length = slowo.length();
                int position = 0;

                while (length > position) {
                    kodZPliku.emplace_back();
                    bitset<16> bity(slowo.substr(position, 16));
                    for (int i = 15; i >= 0; --i) {
                        kodZPliku.back().push_back(bity[i]);
                    }
                    position += 16;
                }
            }
            wejscie.close();

            Odbiornik odbiornik(kodZPliku);
            cout << "Zakodowana wiadomosc otrzymana: " + odbiornik.pobierzKod() << "\n";
            cout << "Poprawnosc slow kodowych: " << boolalpha;
            cout << odbiornik.sprawdzPoprawnosc() << "\n";
            if (!odbiornik.sprawdzPoprawnosc()) {
                odbiornik.naprawBledy();
                cout << "Wiadomosc poprawiona: " << odbiornik.pobierzKod() << "\n";
            }
            Dekoder dekoder(odbiornik.pobierzZakodowanaWiadomosc());
            cout << "Odkodowana wiadomosc: " + dekoder.odkoduj() << "\n";
        } else if (wybor == 4) {
            break;
        }
    }
    return 0;
}

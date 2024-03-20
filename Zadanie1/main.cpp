#include "include/koder.h"
#include "include/dekoder.h"
#include "include/odbiornik.h"
#include <fstream>
#include <iostream>
#include <vector>

using namespace std;

int main() {

    while (true) {
        cout << "Wprowadz 1, 2 lub 3: \n"
                "1. zapisz wiadomosc do pliku wiadomosc.txt\n"
                "2. zakoduj wiadomosc z pliku wiadomosc.txt\n"
                "3. odkoduj wiadomosc z pliku kod.txt w konsoli\n";
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
            ofstream wyjscie("../dane/kod.txt");
            for (const auto& slowo : kodownik.zakoduj()) {
                for (const auto& bit : slowo) {
                    wyjscie << bit;
                }
                wyjscie << endl;
            }
            wyjscie.close();
            cout << "Wiadomosc: " + wiadomosc.pobierzWiadomosc();
            cout << "\nZakodowana wiadomosc: " + kodownik.pobierzZakodowanaWiadomosc() + "\nZapisano do pliku kod.txt";
        } else if (wybor == 3) {
            ifstream wejscie("../dane/kod.txt");
            string slowo;
            vector<vector<bool>> kodZPliku;
            while (!wejscie.eof()) {
                getline(wejscie, slowo);
                if (slowo.length() > 0) {
                    kodZPliku.emplace_back();
                    bitset<16> bity(slowo);
                    for (int i = 15; i >= 0; --i) {
                        kodZPliku.back().push_back(bity[i]);
                    }
                }
            }
            wejscie.close();

            Odbiornik odbiornik(kodZPliku);
            cout << "\nZakodowana wiadomosc otrzymana: " + odbiornik.pobierzKod();
            cout << "\nPoprawnosc slow kodowych: ";
            cout << boolalpha;
            cout << odbiornik.sprawdzPoprawnosc();
            if (!odbiornik.sprawdzPoprawnosc()) {
                odbiornik.naprawBledy();
                cout << "\nWiadomosc poprawiona: " << odbiornik.pobierzKod();
            }
            Dekoder dekoder(odbiornik.pobierzZakodowanaWiadomosc());
            cout << "\nOdkodowana wiadomosc: " + dekoder.odkoduj() << "\n";
        } else if (wybor == 4) {
            // TODO
        } else {
            break;
        }
    }
    return 0;
}

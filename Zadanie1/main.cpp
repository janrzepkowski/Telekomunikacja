#include "include/koder.h"
#include "include/dekoder.h"
#include "include/odbiornik.h"
#include <fstream>
#include <iostream>
#include <vector>

using namespace std;

int main() {
    cout << "Wprowadz 1 lub 2: \n"
            "1. zakoduj wiadomosc z pliku wiadomosc.txt\n"
            "2. odkoduj wiadomosc z pliku kod.txt w konsoli\n";
    int wybor;
    cin >> wybor;
    if (wybor == 1) {
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
    } else if (wybor == 2) {
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
        cout << "\nOdkodowana wiadomosc: " + dekoder.odkoduj();
    } else
        return 0;
    return 0;
}

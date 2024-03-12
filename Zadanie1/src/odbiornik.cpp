#include "../include/odbiornik.h"
#include "../include/macierzH.h"
#include <algorithm>
#include <sstream>
#include <utility>

bool sprawdzPoprawnoscWyrazu(vector<bool> wyraz) {
    bool temp;
    for (int i = 0; i < 8; ++i) {
        temp = wyraz[i + 8];
        for (int j = 0; j < 8; ++j) {
            temp = temp ^ (wyraz[j] & H[i][j]);
        }
        if (temp == 1)
            return false;
    }
    return true;
}

vector<bool> pobierzWektorBledu(vector<bool> wyraz) {
    vector<bool> blad;
    blad.reserve(8);
    int j = 8;
    for (const auto& h : H) {
        bool result = false;
        for (int i = 0; i < 8; ++i) {
            result = result ^ (h[i] & wyraz[i]);
        }
        result = result ^ wyraz[j];
        blad.push_back(result);
        ++j;
    }
    return blad;
}

vector<int> znajdzBlad(vector<bool> wyraz) {
    auto wektorBledu = pobierzWektorBledu(std::move(wyraz));
    vector<int> indeksyBledu;
    indeksyBledu.reserve(2);
    vector<bool> H_kolumna;
    H_kolumna.reserve(8);
    for (int kolumna = 0; kolumna < 16; ++kolumna) {
        for (const auto& wiersz : H) {
            H_kolumna.push_back(wiersz[kolumna]);
        }
        if (std::equal(wektorBledu.begin(), wektorBledu.end(), H_kolumna.begin())) {
            indeksyBledu.push_back(kolumna);
            return indeksyBledu;
        }
        H_kolumna.clear();
    }

    bool H_transponowana[16][8];
    for (int i = 0; i < 8; ++i)
        for (int j = 0; j < 16; ++j)
            H_transponowana[j][i] = H[i][j];

    bool wynik[8];
    for (int i = 0; i < 16; ++i) {
        for (int j = 0; j < 16; ++j) {
            if (i != j) {
                std::fill_n(wynik, 8, false);
                for (int k = 0; k < 8; ++k) {
                    wynik[k] = H_transponowana[i][k] ^ H_transponowana[j][k];
                }
                int n = sizeof(wynik) / sizeof(wynik[0]);
                vector<bool> wynikWektor(wynik, wynik + n);
                if (std::equal(wektorBledu.begin(), wektorBledu.end(), wynikWektor.begin())) {
                    indeksyBledu.push_back(i);
                    indeksyBledu.push_back(j);
                    return indeksyBledu;
                }
                wynikWektor.clear();
            }
        }
    }
    return indeksyBledu;
}

vector<int> Odbiornik::znajdzBladWWyrazie(int numerWyrazu) const {
    return znajdzBlad(kod[numerWyrazu]);
}

Odbiornik::Odbiornik(vector<vector<bool>> kod)
        : kod(std::move(kod)) {
}

bool Odbiornik::sprawdzPoprawnosc() const {
    for (const auto& wyraz : kod) {
        if (!sprawdzPoprawnoscWyrazu(wyraz))
            return false;
    }
    return true;
}

void Odbiornik::naprawBledy() {
    for (int i = 0; i < kod.size(); ++i) {
        if (!sprawdzPoprawnoscWyrazu(kod[i]))
            for (const auto& pozycja : znajdzBladWWyrazie(i))
                kod[i][pozycja] = !kod[i][pozycja];
    }
}

string Odbiornik::pobierzKod() const {
    ostringstream wynik;
    for (const auto &dwaBajty: kod) {
        for (const auto &bit: dwaBajty) {
            wynik << bit;
        }
        wynik << " ";
    }
    return wynik.str();
}

const vector<vector<bool>>& Odbiornik::pobierzZakodowanaWiadomosc() const
{
    return kod;
}
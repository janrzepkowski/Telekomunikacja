#include "../include/dekoder.h"
#include <bitset>
#include <cmath>
#include <sstream>

Dekoder::Dekoder(const vector<vector<bool>> & kod)
{
    oddzielonyKod.reserve(kod.size());
    for (const auto & slowo : kod) {
        oddzielonyKod.emplace_back(begin(slowo), begin(slowo) + 8);
    }
}

const vector<vector<bool>> & Dekoder::pobierzZakodowanaWiadomosc() const
{
    return oddzielonyKod;
}

string Dekoder::pobierzKod() const
{
    ostringstream out;
    for (const auto & slowo : oddzielonyKod) {
        for (const auto& bit : slowo) {
            out << bit;
        }
    }
    return out.str();
}

string Dekoder::odkoduj() const {
    string wynik;
    for (const auto& slowo : oddzielonyKod) {
        int potega = 0;
        int ascii = 0;
        for (const auto& bit : slowo) {
            if (bit != 0)
                ascii += pow(2, potega);
            ++potega;
        }
        char znak = char(ascii);
        wynik += znak;
    }
    return wynik;
}
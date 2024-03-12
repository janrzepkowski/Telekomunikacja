#include "../include/wiadomosc.h"
#include <bitset>
#include <sstream>

using namespace std;

Wiadomosc::Wiadomosc(string wiadomosc)
        : wiadomosc_(std::move(wiadomosc))
{
}

const string& Wiadomosc::pobierzWiadomosc() const { return wiadomosc_; }

vector<bitset<8>> Wiadomosc::doBinarnego() const
{
    vector<bitset<8>> binarnaWiadomosc;
    binarnaWiadomosc.reserve(wiadomosc_.size());
    for (auto const& litera : wiadomosc_) {
        binarnaWiadomosc.emplace_back(bitset<8>{ static_cast<unsigned long long>(litera) });
    }
    return binarnaWiadomosc;
}

string Wiadomosc::binarnieNaTekst() const
{
    ostringstream wynik;
    for (auto const& litera : doBinarnego()) {
        wynik << litera.to_string() << " ";
    }
    return wynik.str();
}
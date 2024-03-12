// plik kodera.cpp
#include "../include/koder.h"
#include "../include/macierzH.h"
#include <bitset>
#include <sstream>

Koder::Koder(Wiadomosc wiadomosc)
        : wiadomosc(std::move(wiadomosc))
{
}

std::vector<bool> pobierzBityParzystosci(const std::bitset<8> & bajtWiadomosci)
{
    std::vector<bool> bityParzystosci;
    bityParzystosci.reserve(8);
    for (const auto& h : H) {
        bool c = false;
        for (int pozycjaBitu = 0; pozycjaBitu < 8; ++pozycjaBitu) {
            c = c ^ (h[pozycjaBitu] & bajtWiadomosci[pozycjaBitu]);
        }
        bityParzystosci.emplace_back(c);
    }
    return bityParzystosci;
}

std::vector<bool> zakodujJedenBajt(const std::bitset<8>& bajtWiadomosci)
{
    std::vector<bool> zakodowany;
    zakodowany.reserve(16);
    for (int pozycjaBitu = 0; pozycjaBitu < 8; ++pozycjaBitu) {
        zakodowany.push_back(bajtWiadomosci[pozycjaBitu]);
    }
    auto bityParzystosci = pobierzBityParzystosci(bajtWiadomosci);
    zakodowany.insert(zakodowany.end(), bityParzystosci.begin(), bityParzystosci.end());
    return zakodowany;
}

std::vector<std::vector<bool>> Koder::zakoduj() const
{
    std::vector<std::vector<bool>> zakodowane;
    zakodowane.reserve(wiadomosc.pobierzWiadomosc().size());
    for (const auto& bajt : wiadomosc.doBinarnego()) {
        zakodowane.push_back(zakodujJedenBajt(bajt));
    }
    return zakodowane;
}

std::string Koder::pobierzZakodowanaWiadomosc() const
{
    auto zakodowanaWiadomosc = zakoduj();
    std::ostringstream out;
    for (const auto& dwaBajty : zakodowanaWiadomosc) {
        for (const auto& bit : dwaBajty) {
            out << bit;
        }
        out << " ";
    }
    return out.str();
}

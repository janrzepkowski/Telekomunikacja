#include <bitset>
#include <string>
#include <vector>

using namespace std;

class Wiadomosc {
private:
    string wiadomosc_;

public:
    explicit Wiadomosc(string wiadomosc);
    const string & pobierzWiadomosc() const;
    vector<bitset<8>> doBinarnego() const;
    string binarnieNaTekst() const;
};
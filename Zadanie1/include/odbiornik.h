#include <string>
#include <vector>

using namespace std;

class Odbiornik {
    vector<vector<bool>> kod;

public:
    explicit Odbiornik(vector<vector<bool>> kod);
    bool sprawdzPoprawnosc() const;
    vector<int> znajdzBladWWyrazie(int numerWyrazu) const;
    void naprawBledy();
    string pobierzKod() const;
    const vector<vector<bool>>& pobierzZakodowanaWiadomosc() const;
};
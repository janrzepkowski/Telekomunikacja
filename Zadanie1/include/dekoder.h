#include <string>
#include <vector>

using namespace std;

class Dekoder {
    vector<vector<bool>> oddzielonyKod;

public:
    explicit Dekoder(const vector<vector<bool>>& kod);
    const vector<vector<bool>> & pobierzZakodowanaWiadomosc() const;
    string pobierzKod() const;
    string odkoduj() const;
};
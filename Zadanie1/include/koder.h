#include "wiadomosc.h"
#include <string>
#include <vector>

class Koder {
    Wiadomosc wiadomosc;

public:
    explicit Koder(Wiadomosc wiadomosc);
    std::vector<std::vector<bool>> zakoduj() const;
    std::string pobierzZakodowanaWiadomosc() const;
};
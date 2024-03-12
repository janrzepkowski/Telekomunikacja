cmake_minimum_required(VERSION 3.27)
project(Zadanie1)

set(CMAKE_CXX_STANDARD 23)

add_executable(Zadanie1
        main.cpp
        src/koder.cpp
        src/dekoder.cpp
        src/wiadomosc.cpp
        src/odbiornik.cpp
        include/koder.h
        include/dekoder.h
        include/macierzH.h
        include/wiadomosc.h
        include/odbiornik.h
)

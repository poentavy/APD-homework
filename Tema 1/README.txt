Poenaru Octavian , 335CA

Am concluzionat ca pentru implementarea temei , trebuia sa paralelizam cam toate functiile de
acolo : sample_grid , rescale , march.
Initial creasem o functie helper numita parallel_operations, in care aveam de gand sa apelez toate
functiile acolo, pentru a fi trimise spre paralelizare in main . 
Ulterior, dupa ce am implementat rescale image , am decis sa nu mai paralelizez si restul fiindca mi s-a
parut un rezultat suficient de stabil , cel putin pe local

Detalii pentru implementare : 
Am inceput prin crearea unei structuri in care mi-am setat campurile
pentru id, imaginea initiala, imaginea finala(scalata) si mutexul.
 In fisierul tema1_par.c pentru paralelizarea functiilor mi-am definit o functie void* rescale_image_thread , care 
 putea primi ca argumente oricate variabile sau niciuna unde am declarat
 imaginea initiala si intreaga structura RescaleThreadData pentru ca ulterior in main sa isi ia 
 programul campurile de care va avea nevoie.
 Verific tot aici daca imaginea finala este nula , iar daca nu 
 Incep sa impart pe fiecare thread operatia de sample_bicubic pentru rescale 
 In main am instantiat imagina new_image cu null pe care o voi folosi sa Verific  daca dim imaginii depasesc RESCALE_X si Y, 
 daca nu , aloc memorie pentru ea folosind malloc pe care o voi elibera mai tarziu.
 Tot aici intializez thread-urile si structura pentru fiecare thread , impreuna cu mutex-ul
 Si dupa creez si setez thread-urile cu variabilele din structura , unde fiecare thread primeste o portiune din imaginea finala.
 La final distrug mutex-ul si dau free la imagine.
 Imaginea scalata este trimisa mai departe si celorlalte functii .

# Unit Tests Hands-On  [![Build Status](https://travis-ci.org/matheusfm/hands-on-unit-tests.svg?branch=master)](https://travis-ci.org/matheusfm/hands-on-unit-tests)

#### Test Pyramid
![Test Pyramid](https://martinfowler.com/bliki/images/testPyramid/test-pyramid.png)

#### Unit Tests Libraries
- [JUnit](https://junit.org)
- [Mockito](http://mockito.org/)


#### Fakes are objects that have working implementations, but not same as production one.
![Fake](https://cdn-images-1.medium.com/max/800/0*snrzYwepyaPu3uC9.png)

##### Stub is an object that holds predefined data and uses it to answer calls during tests.
![Stub](https://cdn-images-1.medium.com/max/800/0*KdpZaEVy6GNnrUpB.png)

##### Mocks are objects that register calls they receive. In test assertion we can verify on Mocks that all expected actions were performed.
![Mock](https://cdn-images-1.medium.com/max/800/0*k7mwTF60slyMxRlm.png)

##### Spies are partial mocks

#### Requisitos
- Buscar primeiro no cache
- Se não existir no cache, buscar no disco
- Se o serviço de cache estiver indisponível, buscar no disco
- Salvar primeiro no disco, onde o ID é gerado, e depois no cache
- Lançar `IllegalArgumentException` se algum parâmetro for `null`
const faker = require('faker');
const uuid = require('uuid/v4');

const partners = [...new Array(20)
  .fill(1)
  .map(partner),
  {...partner(), type: 'platinum'},
  {...partner(), type: 'big data & AI'},


];

function partner() {
  return {
    id: uuid(),
    name: faker.company.companyName(),
    description: faker.lorem.paragraphs(),
    type: faker.random.arrayElement(['silver', 'gold', 'bronze', 'media']),
    www: faker.internet.url(),
    logo: faker.random.arrayElement(['https://2018.confitura.pl/api/resources/photos/350/bf364499-9ac2-4b5c-a6f3-90fe08698272.png', 'https://touk.pl/img/logo.png']),
    published: true
  };
}


module.exports = (app) => {

  app.get('/partners', (req, res) => {
    res.send({
      _embedded: {partners}
    });
  });

  app.get('/partners/:id', (req, res) => {
    res.send(
      partners[0]
    );
  });

  app.get('/partners/search/published', (req, res) => {
    res.send({
      _embedded: {partners}
    });
  });

};

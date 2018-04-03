const faker = require('faker');
const uuid = require('uuid/v4');

module.exports = (app) => {

  app.get('/partners', (req, res) => {
    res.send({
      _embedded: {
        partners:
          new Array(20)
            .fill(1)
            .map(partner)
      }
    });
  });
  app.get('/partners/search/published', (req, res) => {
    res.send({
      _embedded: {
        partners:
          new Array(20)
            .fill(1)
            .map(partner)
      }
    });
  });

  function partner() {
    return {
      id: uuid(),
      name: faker.company.companyName(),
      description: faker.lorem.paragraphs(),
      type: faker.random.arrayElement(['platinum', 'silver', 'gold', 'brown']),
      www: faker.internet.url(),
      logo: faker.random.arrayElement(['https://2018.confitura.pl/api/resources/photos/350/bf364499-9ac2-4b5c-a6f3-90fe08698272.png', 'https://touk.pl/img/logo.png']),
      published: true
    };
  }
};

const faker = require('faker');
const uuid = require('uuid/v4');
const person = require('./person');
module.exports = (app) => {
  app.get('/presentations', (req, res) => {
    res.send({
      _embedded: {
        presentations:
          new Array(10)
            .fill(1)
            .map(presentation)
      }
    });
  });

  function presentation() {
    return {
      id: uuid(),
      title: faker.company.catchPhrase(),
      shortDescription: faker.lorem.paragraph(),
      description: faker.lorem.paragraphs(),
      level: faker.random.arrayElement(['master', 'basic', 'advanced']),
      speaker: person.single(),
      cospeakers: person.list(3),
      tags: [],
      language: faker.random.arrayElement(['polish', 'english']),
      status: 'reported'
    };
  }
};

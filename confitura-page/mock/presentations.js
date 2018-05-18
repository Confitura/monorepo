const faker = require('faker');
const uuid = require('uuid/v4');
const person = require('./person');
const presentations = new Array(20)
  .fill(1)
  .map(presentation);

function presentation() {
  return {
    id: uuid(),
    title: faker.company.catchPhrase(),
    shortDescription: faker.lorem.paragraph(),
    description: faker.lorem.paragraphs(),
    level: faker.random.arrayElement(['master', 'basic', 'advanced']),
    speaker: person.single(),
    cospeakers: person.list(3),
    tags: [{id: 1, name: 'Java'}, {id: 2, name: 'JavaScript'}],
    language: faker.random.arrayElement(['polish', 'english']),
    status: faker.random.arrayElement(['reported', 'accepted'])
  };
}

module.exports = (app) => {
  app.get('/presentations', (req, res) => {
    res.send({_embedded: {presentations}});
  });

  app.get('/presentations/search/accepted', (req, res) => {
    res.send({_embedded: {presentations: presentations.filter(it => it.status === 'accepted')}});
  });

  app.get('/tags', (req, res) => {
    res.send({
      _embedded: {
        tags: [
          {id: '1', name: 'Java'},
          {id: '2', name: 'JavaScript'},
          {id: '3', name: 'Agile'}
        ]
      }
    });
  });
};

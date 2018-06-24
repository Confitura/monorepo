const faker = require('faker');
const uuid = require('uuid/v4');
const person = require('./person');

module.exports = {
  presentation: function () {
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
};

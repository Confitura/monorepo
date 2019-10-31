const faker = require('faker');
const uuid = require('uuid/v4');

module.exports = {
  single: function (type = '') {
    return {
      id: uuid(),
      name: faker.name.findName(),
      email: faker.internet.email(),
      admin: type === 'admin',
      volunteer: type === 'volunteer',
      photo: faker.image.avatar()
    };
  },
  list: function (size, type = '') {
    return new Array(size)
      .fill(1)
      .map(() => this.single(type));
  }

};

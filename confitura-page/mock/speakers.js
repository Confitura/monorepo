const person = require('./person');

const speakers = person.list(20);

module.exports = (app) => {

  app.get('/users/search/speakers', (req, res) => {
    res.send({
      _embedded: {users: speakers}
    });
  });
};

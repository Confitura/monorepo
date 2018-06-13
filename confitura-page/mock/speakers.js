const person = require('./person');

const speakers = person.list(20).map(({name, photo}) => ({name, photo}));

module.exports = (app) => {

  app.get('/users/search/speakers', (req, res) => {
    res.send({
      _embedded: {speakers: speakers}
    });
  });
};

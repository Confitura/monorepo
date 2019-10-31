const person = require('./person');

const speakers = person.list(20).map(({name, photo}) => ({name, photo}));

module.exports = (app) => {

  app.get('/users/search/speakers', (req, res) => {
    res.send({
      _embedded: {speakers: speakers}
    });
  });
  app.get('/users/:id/presentations', (req, res) => {
    res.send({
      _embedded: {
        presentations: [
          {id: '1', title: 'Hello, World!'},
          {id: '2', title: 'Second Presentation'},
        ]
      }
    });
  });
};

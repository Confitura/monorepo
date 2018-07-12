const entities = require('./entities');

const presentations = new Array(100)
  .fill(1)
  .map(entities.presentation);



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

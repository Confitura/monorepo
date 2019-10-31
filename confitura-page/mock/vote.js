const uuid = require('uuid/v4');


module.exports = (app) => {
  app.post('/vote/start/{token}', (req, res) => {
    const token = req.params.token;
    res.send({
      _embedded: {
        votes: [
          {
            'id': uuid(),
            token,
            'order': 0,
            'rate': null
          },
          {
            'id': uuid(),
            token,
            'order': 1,
            'rate': null
          },
        ]
      }
    });
  });

};

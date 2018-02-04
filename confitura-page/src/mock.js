const express = require('express');
const app = express();
let id = 0;


app.use(function (req, res, next) {
  res.header('Access-Control-Allow-Origin', '*');
  res.header('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept');
  next();
});

let person = function (firstName, lastName, type) {
  id += 1;
  return {name: `${firstName} ${lastName}`, id: `${type}_${id}`};
};

app.get('/users/search/:type', function (req, res) {
  const type = req.params.type;
  res.send({
    _embedded: {
      users: [
        person('John', 'Smith', type),
        person('Rob', 'Smith', type),
        person('Samanta', 'Smith', type),
      ]
    }
  });
});

app.get('/pages/:id', function (req, res) {
  res.send({
    id: req.params.id,
    content: `This is my content: ${req.params.id}`
  });
});

app.listen(3000, () => console.log('Example app listening on port 3000!'));


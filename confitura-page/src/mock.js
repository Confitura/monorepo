const express = require('express');
const faker = require('faker');
const app = express();
let id = 0;


app.use(function (req, res, next) {
  res.header('Access-Control-Allow-Origin', '*');
  res.header('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept');
  next();
});

let person = function (firstName, lastName, type) {
  id += 1;
  const name = faker.name.findName();
  return {
    name,
    id: `${type}_${id}`,
    photo: `http://placehold.it/300x300?text=${name}`
  };
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

  const content = `## ${req.params.id}
  ${faker.lorem.paragraphs(5)}
  `;
  res.send({
    id: req.params.id,
    content: content
  });
});

app.listen(3000, () => console.log('Example app listening on port 3000!'));


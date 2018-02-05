const express = require('express');
const faker = require('faker');
// const Base64 = require('base64-js');
const app = express();
let id = 0;


app.use(function (req, res, next) {
  res.header('Access-Control-Allow-Origin', '*');
  res.header('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept, Authorization');
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


app.get('/login/twitter/callback', (req, res) => {
  const user = {
    jti: '1',
    sub: 'John Smith',
    isAdmin: true,
    isNew: false
  };
  const encodedUser = Buffer.from(JSON.stringify(user)).toString('base64');
  res.send(`header.${encodedUser}.signature`);
});


app.get('/login/twitter', (req, res) => {
  res.redirect('http://localhost:8080/login/twitter');
});

app.get('/users/:id', (req, res) => {
  res.send({
    id: req.params.id,
    name: 'John Smith',
    email: 'john@smith.com',
    bio: 'Helo Hello Helo',
    admin: true,
    presentations: [
      {id: '1', title: 'Hello, World!'}
    ]
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


app.listen(3000, () => console.log('Example app listening on port 3000!'));


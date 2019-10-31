const _ = require('lodash');
const uuid = require('uuid/v4');
module.exports = (app) => {


  app.post('/participants/:id/arrived', function (req, res) {
    const participant = {
      id: uuid(),
      size: 'S',
    };
    const id = req.params.id;
    console.log(id);
    if (id.includes('P')) {
      participant.participant = true;
    }
    if (id.includes('A')) {
      participant.admin = true;
    }
    if (id.includes('V')) {
      participant.volunteer = true;
    }
    if (id.includes('S')) {
      participant.speaker = true;
    }
    if (id.includes('W')) {
      participant.hasAcceptedPresentation = true;
    }
    res.send(participant);
  });

  app.get('/time-slots', function (req, res) {
    res.send(embedded('timeSlots', slots));
  });

  app.get('/agenda', function (req, res) {
    res.send(embedded('agendaEntries',
      _.flatten(slots.map(slot =>
        rooms.map(room => anEntry(slot, room, entities.presentation()))
      ))
    ));
  });

};

function embedded(name, values) {
  return ({_embedded: {[name]: values}});
}

function aRoom(name, order) {
  return ({id: uuid(), name, order});
}

function aSlot(start, end) {
  return ({id: uuid(), start, end});
}

function anEntry(timeSlot, room, presentation) {
  const timeSlotId = timeSlot.id;
  const roomId = room.id;
  const presentationId = presentation.id;
  return {id: uuid(), timeSlotId, roomId, presentationId, presentation};
}


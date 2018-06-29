import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {TimeSlot} from '../shared/time-slot.model';
import {AgendaEntry} from '../shared/agenda.model';
import {Room} from '../shared/room.model';
import {BreakpointObserver} from '@angular/cdk/layout';

@Component({
  selector: 'cf-agenda-table',
  templateUrl: './agenda-table.component.html',
  styleUrls: ['./agenda-table.component.scss']
})
export class AgendaTableComponent implements OnInit {
  @Input()
  slots: TimeSlot[];
  @Input()
  agenda: AgendaEntry[][] = [];
  @Input()
  personalAgenda: AgendaEntry[];

  @Output() favoriteChanged: EventEmitter<AgendaEntry> = new EventEmitter();

  _rooms: Room[] = [];
  selectedRooms;

  inPersonalAgenda(entry: AgendaEntry) {
    return !!this.personalAgenda.find(it => it.id === entry.id);
  }

  constructor(
    private breakpointObserver: BreakpointObserver
  ) {
    breakpointObserver.observe(['(min-width: 768px)'])
      .subscribe(result => {
        if (result.matches) {
          this.selectedRooms = this._rooms;
        }
      });
    breakpointObserver.observe(['(max-width: 767px)'])
      .subscribe(result => {
        if (result.matches) {
          this.selectedRooms = [this._rooms[0]];
        }
      });
  }

  ngOnInit() {
  }

  @Input('rooms')
  set rooms(value: Room[]) {
    if (value && value.length > 0) {

      this._rooms = value;
      const isHandheld = this.breakpointObserver.isMatched('(max-width: 767px)');
      if (isHandheld) {
        this.selectedRooms = [value[0]];
      } else {
        this.selectedRooms = value;
      }
    }
  }

  get rooms() {
    return this._rooms;
  }

  set selectedRoom(room: Room) {
    if (room) {
      this.selectedRooms = [room];
    }
  }

  get selectedRoom() {
    if (this.selectedRooms) {
      return this.selectedRooms[0];
    } else {
      return null;
    }
  }

  getTime(time) {
    return '2018-06-30T' + time;
  }
}

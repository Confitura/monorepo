import {Component, OnInit} from '@angular/core';
import {AgendaService} from '../shared/agenda.service';
import {AgendaEntry} from '../shared/agenda.model';

@Component({
  selector: 'cf-personal-agenda',
  templateUrl: './personal-agenda.component.html',
  styleUrls: ['./personal-agenda.component.scss']
})
export class PersonalAgendaComponent implements OnInit {
  agenda: AgendaEntry[];

  constructor(private agendaService: AgendaService) {
    this.agendaService.getPersonalAgenda().subscribe(it => this.agenda = it);
  }

  ngOnInit() {
  }

}

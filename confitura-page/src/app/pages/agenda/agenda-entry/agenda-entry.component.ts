import {Component, Input, OnInit} from '@angular/core';
import {AgendaEntry} from '../shared/agenda.model';

import './agenda-entry.component.scss';

@Component({
  templateUrl: './agenda-entry.component.html',
  selector: 'cf-entry'
})
export class AgendaEntryComponent implements OnInit {
  @Input() entry: AgendaEntry;

  ngOnInit(): void {
  }

}

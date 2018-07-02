import {Component, EventEmitter, Input, Output} from '@angular/core';
import {AgendaEntry} from '../shared/agenda.model';
import {AgendaService} from '../shared/agenda.service';
import {CurrentUser} from '../../../core/security/current-user.service';

@Component({
  templateUrl: './agenda-entry-display.component.html',
  selector: 'cf-agenda-entry-display',
  styleUrls: ['./agenda-entry-display.component.scss']
})
export class AgendaEntryDisplayComponent {
  @Input() entry: AgendaEntry;
  @Output() favoriteChanged: EventEmitter<AgendaEntry> = new EventEmitter();
  @Input() showActions = true;


  constructor(private service: AgendaService, private user: CurrentUser) {
    if (!user.isAvailable()) {
      this.showActions = false;
    }
  }

  addToPersonalAgenda() {
    this.service.addToPersonalAgenda(this.entry.id).subscribe(() => this.favoriteChanged.emit(this.entry));
  }
}

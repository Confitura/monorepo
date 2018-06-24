import {Component, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {TimeSlot} from './shared/time-slot.model';
import {Room} from './shared/room.model';
import {AgendaService} from './shared/agenda.service';
import {Tag} from '../../profile/shared/tag.model';
import {Observable} from 'rxjs/Observable';
import {PresentationService} from '../../profile/shared/presentation.service';
import {AgendaEntry} from './shared/agenda.model';

@Component({
  templateUrl: './agenda.component.html',
  styleUrls: ['./agenda.component.scss']
})
export class AgendaComponent implements OnInit, OnChanges {

  slots: TimeSlot[] = [];
  rooms: Room[] = [];
  selectedRooms: Room[] = [];
  activeRooms: any = null;
  agenda: AgendaEntry[][] = [];
  filteredAgenda: AgendaEntry[][] = [];
  tags: Tag[];

  selectedTag: Tag = null;
  selectedLevels = {
    'beginner': true,
    'advanced': true
  };
  selectedLanguages = {
    'polish': true,
    'english': true
  };

  constructor(
    private  service: AgendaService,
    private presentationService: PresentationService
  ) {
  }

  selectTag(tag: Tag) {
    this.selectedTag = tag;
    this.filter();
  }

  selectLevel(level: string) {
    this.selectedLevels[level] = !this.selectedLevels[level];
    this.filter();
  }

  isLevelSelected(level: string) {
    return !!this.selectedLevels[level];
  }

  selectLanguage(language: string) {
    this.selectedLanguages[language] = !this.selectedLanguages[language];
    this.filter();
  }

  isLanguageSelected(language: string) {
    return !!this.selectedLanguages[language];
  }

  ngOnInit(): void {
    this.getTags().subscribe(it => this.tags = it);
    this.refresh();
  }

  filter() {
    this.filteredAgenda = this.agenda.map(timeSlot => {
      return timeSlot.map(entry => {
        if (!entry.presentationId) {
          return entry;
        }

        if (this.hasSelectedLang(entry)
          && this.hasSelectedLevel(entry)
          && this.hasSelectedTag(entry)) {
          return entry;
        }

        return this.emptyEntry();
      });
    });

  }

  private emptyEntry() {
    return new AgendaEntry();
  }

  ngOnChanges(changes: SimpleChanges): void {
    console.log('changes');
    console.log(changes);
  }

  getTags(): Observable<Tag[]> {
    return this.presentationService.allTags();
  }

  refresh() {
    // console.log(this.refresh());
    this.service.getAgenda().subscribe((it: any) => {
      this.rooms = it.rooms;
        this.slots = it.slots;
      this.agenda = it.agenda;

      if (this.activeRooms == null) {
        this.activeRooms = {};
        for (const room of this.rooms) {
          this.activeRooms[room.id] = true;
        }
        this.selectedRooms = this.rooms.filter(room => this.isActive(room));
      }
      this.filter();

    });
  }

  selectRoom(room: Room) {
    this.activeRooms[room.id] = !this.activeRooms[room.id];
    this.selectedRooms = this.rooms.filter(currentRoom => this.isActive(currentRoom));
  }

  isActive(room: Room) {
    return true;
    // return this.activeRooms[room.id];
  }

  private hasSelectedLang(entry: AgendaEntry) {
    return !!this.selectedLanguages[entry.presentation.language];
  }

  private hasSelectedLevel(entry: AgendaEntry) {
    return !!this.selectedLevels[entry.presentation.level];
  }

  private hasSelectedTag(entry: AgendaEntry) {
    return !this.selectedTag ||
      (entry.tags && entry.tags.some(tag => tag.id === this.selectedTag.id));
  }
}

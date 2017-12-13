import {Component} from '@angular/core';
import {PersonModalService} from './person-modal.service';
import {Presentation} from '../../profile/shared/presentation.model';
import {Router} from '@angular/router';
import {User} from '../../core/user/user.model';

@Component({
  selector: 'cf-person-modal',
  templateUrl: './person-modal.component.html',
  styleUrls: ['./person-modal.component.scss']
})
export class PersonModalComponent {
  model: User = new User();

  constructor(service: PersonModalService, private router: Router) {
    service.changed.subscribe((user: User) => {
      this.model = user;
      if (user) {
        this.openModal();
      } else {
        this.closeModal();
      }
    });
  }

  goTo(presentation: Presentation) {
    this.closeModal();
    this.router.navigate(['/admin2/presentations'], {fragment: presentation.id});
  }

  addPresentation() {
    this.closeModal();
    this.router.navigate([`/user/${this.model.id}/presentation`]);
  }

  private closeModal() {
    $('.modal').modal('hide');
  }

  private openModal() {
    $('.modal').modal({show: true});
  }


}

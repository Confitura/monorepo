import {Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {Router} from '@angular/router';
import {User} from '../../../core/user/user.model';
import {UserService} from '../../../core/user/user.service';

@Component({
  templateUrl: './user-list.component.html'
})
export class UserListComponent implements OnInit {
  list: Observable<User[]>;

  constructor(private service: UserService,
              private router: Router) {

  }

  view(user: User) {
    this.router.navigate([`/profile/${user.id}`]);

  }

  ngOnInit(): void {
    this.list = this.service.getAll();
  }

  addPresentationTo(user: User) {
    this.router.navigate([`/user/${user.id}/presentation`]);
  }

  markAsVolunteer(user: User) {
    this.service.markAsVolunteer(user, true)
      .subscribe(() => this.ngOnInit());
  }

  notVolunteer(user: User) {
    this.service.markAsVolunteer(user, false)
      .subscribe(() => this.ngOnInit());
  }
}

import {Component, OnInit, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {User} from '../../../core/user/user.model';
import {UserService} from '../../../core/user/user.service';
import {MatPaginator, MatSort, MatTable, MatTableDataSource} from '@angular/material';

@Component({
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss']
})
export class UserListComponent implements OnInit {
  dataSource = new MatTableDataSource<User>([]);
  filter = {type: '*', text: ''};
  @ViewChild(MatTable) table: MatTable<User>;
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  columns = ['name', 'email', 'role', 'actions'];


  constructor(private service: UserService,
              private router: Router) {
  }


  ngOnInit(): void {

    this.service.getAll()
      .subscribe(users => {
        this.dataSource = new MatTableDataSource<User>(users);
        this.dataSource.sort = this.sort;
        this.dataSource.paginator = this.paginator;
        this.dataSource.filterPredicate = (data: User) => {
          return this.isOfSelectedType(data) && this.matchesText(data);
        };
      });
  }

  isOfSelectedType(user: User) {
    switch (this.filter.type) {
      case 'a' :
        return user.admin;
      case 'v' :
        return user.volunteer;
      default :
        return true;
    }
  }

  matchesText(user: User) {
    return [user.name, user.email]
      .map(it => it === null ? '' : it)
      .map(it => it.toLowerCase())
      .some(it => it.includes(this.filter.text.toLowerCase()));
  }

  view(user: User) {
    this.router.navigate([`/profile/${user.id}`]);

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

  getRoleFor(user: User): string {
    if (user.admin) {
      return 'admin';
    } else if (user.volunteer) {
      return 'volunteer';
    } else {
      return '';
    }
  }

  doFilter() {
    this.dataSource.filter = 'changed';
  }
}

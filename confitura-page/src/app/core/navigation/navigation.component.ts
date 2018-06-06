import {Component, OnInit} from '@angular/core';
import {CurrentUser} from '../security/current-user.service';
import {LoginService} from '../security/login.service';
import {MenuItem} from './menu-item.model';
import {Router} from '@angular/router';

@Component({
  selector: 'cf-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.scss']
})
export class NavigationComponent implements OnInit {
  private loggedIn: boolean;
  menu: MenuItem[] = [
    {label: 'about us', url: '/about'},
    {label: 'partners', url: '/partners'},
    {label: 'presentations', url: '/presentations'},
    {label: 'speakers', url: '/speakers'},
    {label: 'profile', action: () => this.goToProfile(), show: () => this.loggedIn},
    {
      label: 'admin', show: () => this.currentUser.isPrivileged(),
      children: [
        {label: 'scanner', url: '/admin/scanner', show: () => this.currentUser.isPrivileged()},
        {label: 'participants', url: '/admin/participants', show: () => this.currentUser.isAdmin()},
        {label: 'vouchers', url: '/admin/vouchers', show: () => this.currentUser.isAdmin()},
        {label: 'manage schedule', url: '/admin/agenda', show: () => this.currentUser.isAdmin()},
        {label: 'users', url: '/admin/users', show: () => this.currentUser.isAdmin()},
        {label: 'votes', url: '/admin/votes', show: () => this.currentUser.isAdmin()},
      ]
    },
    {label: 'workshops', url: '/workshops'},
    {label: 'speaker\'s zone', url: '/login', show: () => !this.loggedIn},
    {label: 'logout', action: () => this.logout(), show: () => this.loggedIn},
    {label: 'FAQ', url: '/faq', clazz: 'pink'},
    {label: 'Vote 4 Papers', url: '/v4p', clazz: 'pink', show: () => false},
  ];


  constructor(private currentUser: CurrentUser,
              private login: LoginService,
              private router: Router) {
  }

  ngOnInit(): void {
    this.loggedIn = this.currentUser.isAvailable();
    this.currentUser
      .onLogin.subscribe(() => this.loggedIn = this.currentUser.isAvailable());
  }

  logout() {
    this.login.logout();
  }

  goToProfile() {
    this.router.navigate([`/profile/${this.currentUser.get().jti}`]);

  }


}

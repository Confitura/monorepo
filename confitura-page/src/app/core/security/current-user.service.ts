import {EventEmitter, Injectable} from '@angular/core';
import {JwtUser} from '../../pages/home/jwt-user.model';
import {Base64} from 'js-base64';
import {Router} from '@angular/router';

@Injectable()
export class CurrentUser {

  onLogin: EventEmitter<JwtUser> = new EventEmitter<JwtUser>();

  constructor(private router: Router) {
    if (this.isAvailable()) {
      console.log('user available ', this.get());
      this.onLogin.emit(this.get());
    }
  }

  set(token: string) {
    const user = Base64.decode(token.split('.')[1]);
    localStorage.setItem('user', user);
    localStorage.setItem('token', token);
    this.onLogin.emit(this.get());
  }

  get(): JwtUser {
    if (this.isAvailable()) {
      return JSON.parse(localStorage.getItem('user')) as JwtUser;
    } else {
      return new JwtUser();
    }
  }

  getToken() {
    return localStorage.getItem('token');
  }

  isAvailable() {
    return this.getToken() != null;
  }

  isAdmin() {
    return this.isAvailable() && this.get().isAdmin;
  }

  isVolunteer() {
    return this.isAvailable() && this.get().isVolunteer;
  }

  isSpeaker() {
    return this.isAvailable() && this.get().isSpeaker;
  }

  isPrivileged() {
    return this.isVolunteer() || this.isAdmin();
  }

  is(id: string) {
    return id && id === this.get().jti;
  }


  logout() {
    localStorage.clear();
    this.onLogin.emit(this.get());
    this.router.navigate(['/']);
  }
}

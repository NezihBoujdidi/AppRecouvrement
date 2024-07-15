import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly loggedInKey = 'loggedIn';

  constructor() {}
  login(username: string, password: string): boolean {
    // Add your actual authentication logic here.
    if (username === 'admin' && password === 'admin') {
      sessionStorage.setItem(this.loggedInKey, 'true');
      return true;
    } else {
      return false;
    }
  }

  logout(): void {
    sessionStorage.removeItem(this.loggedInKey);
  }

  isLoggedIn(): boolean {
    return sessionStorage.getItem(this.loggedInKey) === 'true';
  }
}

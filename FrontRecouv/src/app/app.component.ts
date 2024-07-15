import { Component, OnInit } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { AuthService } from './auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  isCollapsed: boolean = false;
  ishidden: boolean = false;

  constructor(private router: Router, private authService: AuthService) {}

  ngOnInit(): void {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.updateActiveState();
      }
    });
    this.updateActiveState();
  }

  updateActiveState(): void {
    const currentRoute = this.router.url;
    const isAuthRoute = currentRoute.includes('login') ;

    this.ishidden = !this.authService.isLoggedIn() || isAuthRoute;

  }

  toggleSidebar() {
    this.isCollapsed = !this.isCollapsed;
  }
}

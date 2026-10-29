import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Entete } from './entete';

describe('Entete', () => {
  let component: Entete;
  let fixture: ComponentFixture<Entete>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Entete],
    }).compileComponents();

    fixture = TestBed.createComponent(Entete);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

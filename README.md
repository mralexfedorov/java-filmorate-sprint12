# java-filmorate
## er-diagramm by dbdiagram.io
![filmorate](https://user-images.githubusercontent.com/95481854/198903998-c5b6acb4-1bca-4e52-bf55-41eb9d4b5d32.png)

## description by dbdiagram.io
```
Table public.users {
  user_id int [pk, increment]
  email varchar(255) [not null]
  login varchar(255) [not null]
  name varchar(255) [default: 'common']
  birthday date [not null]
  
  Indexes {
    (user_id) [pk]
  }
}

Table public.user_friends {
  user_id int [ref: <> users.user_id]
  friend_id int [ref: <> users.user_id]
  
  Indexes {
    (user_id, friend_id) [pk]
  }
}

Table public.films {
  film_id int [pk, increment]
  name varchar [not null]
  description varchar [not null]
  release_date date [not null]
  duration int [not null]
  rate int [not null]
  mpa_id varchar [ref: > mpa.id]
  
  Indexes {
    (film_id) [pk]
    (mpa_id)
  }
}

Table public.film_genres {
  film_id int [ref: > films.film_id]
  genre_id int [ref: > genres.id]
  
  Indexes {
    (film_id, genre_id) [pk]
  }
}

Table public.film_likes {
  film_id int [ref: > films.film_id]
  user_id int [ref: > users.user_id]
  
  Indexes {
    (film_id, user_id) [pk]
  }
}

Table public.genres {
  id int [pk, increment]
  name varchar(255)
  
  Indexes {
    (id) [pk]
  }
}

Table public.mpa {
  id int [pk, increment]
  name varchar(255)
  description varchar(255)
  
  Indexes {
    (id) [pk]
  }  
}
```

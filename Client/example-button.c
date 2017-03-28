#include <gtk/gtk.h>

GdkPixbuf *create_pixbuf(const gchar * filename) {
    
   GdkPixbuf *pixbuf;
   GError *error = NULL;
   pixbuf = gdk_pixbuf_new_from_file(filename, &error);
   
   if (!pixbuf) {
       
      fprintf(stderr, "%s\n", error->message);
      g_error_free(error);
   }

   return pixbuf;
}

int main(int argc, char *argv[]) {
    
  GtkWidget *window;
  GtkWidget *halign;
  GtkWidget *btn;
  
  GtkWidget *table;
  GtkWidget *label1;
  GtkWidget *entry1;
  
  GdkPixbuf *icon;

  gtk_init(&argc, &argv);

  window = gtk_window_new(GTK_WINDOW_TOPLEVEL);
  gtk_window_set_title(GTK_WINDOW(window), "Test de fenêtre de connexion");
  gtk_window_set_default_size(GTK_WINDOW(window), 460, 300);
  gtk_container_set_border_width(GTK_CONTAINER(window), 15);
  gtk_window_set_position(GTK_WINDOW(window), GTK_WIN_POS_CENTER);
  
  icon = create_pixbuf("lettreScrabbleS.png");  
  gtk_window_set_icon(GTK_WINDOW(window), icon);
  
  table = gtk_table_new(3, 2, FALSE);
  gtk_container_add(GTK_CONTAINER(window), table);
  
  label1 = gtk_label_new("Name");
  gtk_table_attach(GTK_TABLE(table), label1, 0, 1, 0, 1, 
      GTK_FILL | GTK_SHRINK, GTK_FILL | GTK_SHRINK, 5, 5);
  entry1 = gtk_entry_new();
  gtk_table_attach(GTK_TABLE(table), entry1, 1, 2, 0, 1, 
      GTK_FILL | GTK_SHRINK, GTK_FILL | GTK_SHRINK, 5, 5);

  halign = gtk_alignment_new(0, 0, 0, 0);
  gtk_container_add(GTK_CONTAINER(window), halign);

  btn = gtk_button_new_with_label("Quit");
  gtk_widget_set_size_request(btn, 70, 30);

  gtk_container_add(GTK_CONTAINER(halign), btn);

  g_signal_connect(G_OBJECT(btn), "clicked", 
      G_CALLBACK(gtk_main_quit), G_OBJECT(window));

  g_signal_connect(G_OBJECT(window), "destroy", 
      G_CALLBACK(gtk_main_quit), NULL);

  gtk_widget_show_all(window);

  g_object_unref(icon);    
  
  gtk_main();

  return 0;
}

# Réponses aux questions

**Q1.** Sans `user_id`, rien ne relie un contact à son propriétaire : toutes les requêtes
renverraient les contacts de tout le monde. `WHERE user_id = ?` permet de filtrer par
l'utilisateur connecté.

**Q2.** Sans `ON DELETE CASCADE`, MySQL refuserait de supprimer un utilisateur qui a
des contacts (erreur de clé étrangère). Il faudrait supprimer les contacts à la main
d'abord. Sans clé étrangère du tout, on aurait des contacts « orphelins ».

**Q3.** L'`id` seul identifie le contact, pas son propriétaire. Un utilisateur
malveillant qui connaît l'id d'un contact d'un autre pourrait le modifier ou le
supprimer (faille IDOR). `AND user_id = ?` garantit que la requête ne touche que ses
propres contacts.
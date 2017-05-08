#!/usr/bin/env perl
# vim:set et ai sw=4:
use strict;
use warnings;

use File::Slurp qw(read_file write_file);

my $infile = "./src/main/resources/com/google/gwt/user/client/ui/DesktopStyle.css";
my $outfile = $infile . ".out";
# bundle images directly from gwt jar
my $res_prefix = "com/google/gwt/user/theme/standard/public/gwt/standard/";
my $css_head = ""; # will contain css var defs like "@url variable res-method;"
my %res_method_by_url = ();
my %url_by_res_method = ();

my $a = read_file($infile);
while($a =~ s/url\(["']?([^"\r\n]+)["']?\)/process_url($1)/gei) {}

$a = $css_head . $a;

write_file($outfile, $a);
print STDERR "wrote css to $outfile".$/;

sub process_url {
    my ($url) = @_;
    $url =~ s/^['"]//;
    $url =~ s/["']$//;
    # re-use resource access method if we already created one for that url
    my $res_method_name = exists $res_method_by_url{$url} ? $res_method_by_url{$url} : undef;
    # otherwise generate a new access method for the url resource
    if(!defined($res_method_name)) {
        $res_method_name = "r".uc(substr($url,0,1)).substr($url,1);
        $res_method_name =~ s/[\/\.]/_/g;
        $res_method_name =~ s/_+(.)/uc($1)/eg;
        my $res_method_name_suffix = "";
        while(exists $url_by_res_method{$res_method_name.$res_method_name_suffix}) {
            if($res_method_name_suffix eq "") {
                $res_method_name_suffix = 2;
            } else {
                $res_method_name_suffix++;
            }
        } 
        $res_method_name .= $res_method_name_suffix;
        $res_method_by_url{$url} = $res_method_name;
        $url_by_res_method{$res_method_name} = $url;
        $css_head .= "/* was: $url */".$/;
        $css_head .= "\@url c_$res_method_name $res_method_name;".$/;
        print STDOUT "\@Source(\"$res_prefix$url\")".$/;
        print STDOUT "DataResource $res_method_name();".$/;
    }
    return "c_$res_method_name";
}

/**
 * Javascript formatter...
 */
function foo()
{
  hello();
};
if (5 > 0)
{
  alert('hello!');
}
else
{
  alert('oops!');
}

for (var i = Things.length - 1; i >= 0; i--)
{
  // Do something...
}

do
{
  document.write("The hour is " + hour);
  hour = hour + 1;
} while (hour >= 12);

switch (theDay)
{
  case 5:
    document.write("Friday");
    break;
  case 6:
    document.write("Saturday");
    break;
  case 0:
    document.write("Sunday");
    break;
  default:
    document.write("Donno :(");
}
/**
 * My function...
 */
function message()
{
  try
  {
    alert("Welcome guest!");
  }
  catch(err)
  {
    if (1 > 0)
      a();
    txt="There was an error on this page.\n\n";
    txt+="Error description: " + err.description + "\n\n";
    txt+="Click OK to continue.\n\n";
    alert(txt);
  }
  finally
  {
    alert('finally');
  }
}
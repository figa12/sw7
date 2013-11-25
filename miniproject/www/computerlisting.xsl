<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
  <h2>Computers for sale on eBay</h2>
    <table border="1">
      <tr bgcolor="#9acd32">
        <th>Current bid</th>
        <th>Memory</th>
        <th>Hard drive</th>
        <th>CPU</th>
      </tr>
      <xsl:for-each select="root/listing">
      <tr>
        <td><xsl:value-of select="auction_info/current_bid"/></td>
        <td><xsl:value-of select="item_info/memory"/></td>
        <td><xsl:value-of select="item_info/hard_drive"/></td>
        <td><xsl:value-of select="item_info/cpu"/></td>
      </tr>
      </xsl:for-each>
    </table>
</xsl:template>
</xsl:stylesheet>